package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.CameraShakeS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
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
            int minX = Mth.floor(explosion.position.getX() - radius - 1.0F);
            int maxX = Mth.floor(explosion.position.getX() + radius + 1.0F);
            int minY = Mth.floor(explosion.position.getY() - radius - 1.0F);
            int maxY = Mth.floor(explosion.position.getY() + radius + 1.0F);
            int minZ = Mth.floor(explosion.position.getZ() - radius - 1.0F);
            int maxZ = Mth.floor(explosion.position.getZ() + radius + 1.0F);

            if (explosion.age == 0) {
                event.level.playSound(null, explosion.position.getX(), explosion.position.getY(), explosion.position.getZ(),
                        JJKSounds.EXPLOSION.get(), SoundSource.MASTER, 10.0F, 1.0F);
                AABB bounds = new AABB(explosion.position.getX() - (explosion.radius * 2.0F), explosion.position.getY() - (explosion.radius * 2.0F),
                        explosion.position.getZ() - (explosion.radius * 2.0F),
                        explosion.position.getX() + (explosion.radius * 2.0F), explosion.position.getY() + (explosion.radius * 2.0F),
                        explosion.position.getZ() + (explosion.radius * 2.0F));

                for (ServerPlayer player : event.level.getEntitiesOfClass(ServerPlayer.class, bounds)) {
                    PacketHandler.sendToClient(new CameraShakeS2CPacket(1.0F, 5.0F, explosion.duration), player);
                }
            }

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        double distance = (x - explosion.position.getX()) * (x - explosion.position.getX()) +
                                (y - explosion.position.getY()) * (y - explosion.position.getY()) +
                                (z - explosion.position.getZ()) * (z - explosion.position.getZ());

                        if (distance <= radius * radius) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = event.level.getBlockState(pos);

                            for (Entity entity : event.level.getEntities(explosion.source, new AABB(pos).inflate(1.0D))) {
                                entity.hurt(explosion.ability == null || explosion.source == null ? event.level.damageSources().explosion(explosion.source, null) :
                                        JJKDamageSources.jujutsuAttack(explosion.source, explosion.ability), explosion.radius);
                            }

                            if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > -1.0F && !state.isAir()) {
                                if (event.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                                    event.level.destroyBlock(pos, false);

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

    public static void spawn(ResourceKey<Level> dimension, BlockPos position, float radius, int duration, @Nullable LivingEntity source, @Nullable Ability ability) {
        explosions.add(new ExplosionData(dimension, position, radius, duration, source, ability));
    }

    private static class ExplosionData {
        private final ResourceKey<Level> dimension;
        private final BlockPos position;
        private final float radius;
        private final int duration;
        private int age;
        private final @Nullable LivingEntity source;
        private final @Nullable Ability ability;

        public ExplosionData(ResourceKey<Level> dimension, BlockPos position, float radius, int duration, @Nullable LivingEntity source, @Nullable Ability ability) {
            this.dimension = dimension;
            this.position = position;
            this.radius = radius;
            this.duration = duration;
            this.source = source;
            this.ability = ability;
        }
    }
}