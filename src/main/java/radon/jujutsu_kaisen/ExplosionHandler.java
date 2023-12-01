package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.CameraShakeS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExplosionHandler {
    private static final List<ExplosionData> explosions = new CopyOnWriteArrayList<>();

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.CLIENT) return;

        List<ExplosionData> remove = new ArrayList<>();

        for (ExplosionData explosion : explosions) {
            if (event.level.dimension() != explosion.dimension) return;

            float radius = Math.min(explosion.radius, explosion.radius * (0.5F + ((float) explosion.age / explosion.duration)));
            int minX = Mth.floor(explosion.position.x() - radius - 1.0F);
            int maxX = Mth.floor(explosion.position.x() + radius + 1.0F);
            int minY = Mth.floor(explosion.position.y() - radius - 1.0F);
            int maxY = Mth.floor(explosion.position.y() + radius + 1.0F);
            int minZ = Mth.floor(explosion.position.z() - radius - 1.0F);
            int maxZ = Mth.floor(explosion.position.z() + radius + 1.0F);

            if (explosion.age == 0) {
                event.level.playSound(null, explosion.position.x(), explosion.position.y(), explosion.position.z(),
                        SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 10.0F, 1.0F);
                AABB bounds = new AABB(explosion.position.x() - (explosion.radius * 2.0F), explosion.position.y() - (explosion.radius * 2.0F),
                        explosion.position.z() - (explosion.radius * 2.0F),
                        explosion.position.x() + (explosion.radius * 2.0F), explosion.position.y() + (explosion.radius * 2.0F),
                        explosion.position.z() + (explosion.radius * 2.0F));

                for (ServerPlayer player : event.level.getEntitiesOfClass(ServerPlayer.class, bounds)) {
                    PacketHandler.sendToClient(new CameraShakeS2CPacket(1.0F, 5.0F, explosion.duration), player);
                }
            }

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        double distance = (x - explosion.position.x()) * (x - explosion.position.x()) +
                                (y - explosion.position.y()) * (y - explosion.position.y()) +
                                (z - explosion.position.z()) * (z - explosion.position.z());

                        double adjusted = radius * ((double) explosion.age / explosion.duration);

                        if (distance <= adjusted * adjusted) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = event.level.getBlockState(pos);

                            for (Entity entity : HelperMethods.getEntityCollisions(event.level, AABB.ofSize(pos.getCenter(), 1.0D, 1.0D, 1.0D))) {
                                if (!entity.ignoreExplosion()) {
                                    double d12 = Math.sqrt(entity.distanceToSqr(explosion.position)) / explosion.radius;

                                    if (d12 <= 1.0D) {
                                        double d5 = entity.getX() - explosion.position.x();
                                        double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - explosion.position.y();
                                        double d9 = entity.getZ() - explosion.position.z();
                                        double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

                                        if (d13 != 0.0D) {
                                            d5 /= d13;
                                            d7 /= d13;
                                            d9 /= d13;
                                            double d14 = Explosion.getSeenPercent(explosion.position, entity);
                                            double d10 = (1.0D - d12) * d14;
                                            entity.hurt(explosion.source, (float) ((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)explosion.radius + 1.0D)) * explosion.damage);

                                            double d11;

                                            if (entity instanceof LivingEntity living) {
                                                d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(living, d10);
                                            } else {
                                                d11 = d10;
                                            }
                                            d5 *= d11;
                                            d7 *= d11;
                                            d9 *= d11;
                                            Vec3 vec31 = new Vec3(d5, d7, d9);
                                            entity.setDeltaMovement(entity.getDeltaMovement().add(vec31).normalize());
                                        }
                                    }
                                }
                            }

                            if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE && !state.isAir()) {
                                if (event.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                                    event.level.destroyBlock(pos, false);

                                    if (explosion.fire) {
                                        if (HelperMethods.RANDOM.nextInt(3) == 0 && event.level.getBlockState(pos).isAir() && event.level.getBlockState(pos.below()).isSolidRender(event.level, pos.below())) {
                                            event.level.setBlockAndUpdate(pos, BaseFireBlock.getState(event.level, pos));
                                        }
                                    }
                                    if (HelperMethods.RANDOM.nextInt(10) == 0) {
                                        ((ServerLevel) event.level).sendParticles(ParticleTypes.EXPLOSION, x, y, z, 0,
                                                0.0D, 0.0D, 0.0D, 0.0D);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            explosion.age++;

            if (explosion.age >= explosion.duration) {
                remove.add(explosion);
            }
        }
        explosions.removeAll(remove);
    }

    public static void spawn(ResourceKey<Level> dimension, Vec3 position, float radius, int duration, @Nullable LivingEntity instigator, DamageSource source, boolean fire) {
        explosions.add(new ExplosionData(dimension, position, radius, duration, 1.0F, instigator, source, fire));
    }

    public static void spawn(ResourceKey<Level> dimension, Vec3 position, float radius, int duration, float damage, @Nullable LivingEntity instigator, DamageSource source, boolean fire) {
        explosions.add(new ExplosionData(dimension, position, radius, duration, damage, instigator, source, fire));
    }

    private static class ExplosionData {
        private final ResourceKey<Level> dimension;
        private final Vec3 position;
        private final float radius;
        private final int duration;
        private final float damage;
        private int age;
        private final @Nullable LivingEntity instigator;
        private final DamageSource source;
        private final boolean fire;

        public ExplosionData(ResourceKey<Level> dimension, Vec3 position, float radius, int duration, float damage, @Nullable LivingEntity instigator, DamageSource source, boolean fire) {
            this.dimension = dimension;
            this.position = position;
            this.radius = radius;
            this.duration = duration;
            this.damage = damage;
            this.instigator = instigator;
            this.source = source;
            this.fire = fire;
        }
    }
}