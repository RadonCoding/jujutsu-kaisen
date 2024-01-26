package radon.jujutsu_kaisen;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.CameraShakeS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.ParticleUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExplosionHandler {
    private static final List<ExplosionData> explosions = new CopyOnWriteArrayList<>();

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> pDropPositionArray, ItemStack pStack, BlockPos pPos) {
        int i = pDropPositionArray.size();

        for (int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = pDropPositionArray.get(j);
            ItemStack stack = pair.getFirst();

            if (ItemEntity.areMergable(stack, pStack)) {
                ItemStack merged = ItemEntity.merge(stack, pStack, 16);
                pDropPositionArray.set(j, Pair.of(merged, pair.getSecond()));

                if (pStack.isEmpty()) {
                    return;
                }
            }
        }
        pDropPositionArray.add(Pair.of(pStack, pPos));
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.side == LogicalSide.CLIENT) return;

        List<ExplosionData> remove = new ArrayList<>();

        for (ExplosionData explosion : explosions) {
            if (event.level.dimension() != explosion.dimension) return;

            float diameter = explosion.radius * 2.0F;

            Explosion current = new Explosion(event.level, explosion.instigator, 0.0D, 0.0D, 0.0D, 0.0F, List.of());

            if (explosion.age == 0) {
                event.level.playSound(null, explosion.position.x, explosion.position.y, explosion.position.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, diameter, 1.0F);

                AABB bounds = new AABB(explosion.position.x - diameter,
                        explosion.position.y - diameter,
                        explosion.position.z - diameter,
                        explosion.position.x + diameter,
                        explosion.position.y + diameter,
                        explosion.position.z + diameter);

                for (ServerPlayer player : event.level.getEntitiesOfClass(ServerPlayer.class, bounds)) {
                    PacketHandler.sendToClient(new CameraShakeS2CPacket(1.0F, 5.0F, explosion.duration), player);
                }

                List<Entity> entities = event.level.getEntities(explosion.instigator, new AABB(Mth.floor(explosion.position.x - diameter - 1.0F),
                        Mth.floor(explosion.position.y - diameter - 1.0F),
                        Mth.floor(explosion.position.z - diameter - 1.0F),
                        Mth.floor(explosion.position.x + diameter + 1.0F),
                        Mth.floor(explosion.position.y + diameter + 1.0F),
                        Mth.floor(explosion.position.z + diameter + 1.0F)));
                ForgeEventFactory.onExplosionDetonate(event.level, current, entities, diameter);

                for (Entity entity : entities) {
                    if (!entity.ignoreExplosion()) {
                        double d12 = Math.sqrt(entity.distanceToSqr(explosion.position)) / (double) diameter;

                        if (d12 <= 1.0D) {
                            double d5 = entity.getX() - explosion.position.x;
                            double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - explosion.position.y;
                            double d9 = entity.getZ() - explosion.position.z;
                            double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

                            if (d13 != 0.0D) {
                                d5 /= d13;
                                d7 /= d13;
                                d9 /= d13;
                                double d14 = Explosion.getSeenPercent(explosion.position, entity);
                                double d10 = (1.0D - d12) * d14;
                                entity.hurt(explosion.source, (float) ((int)((d10 * d10 + d10) / 2.0D * 7.0D * explosion.radius + 1.0D)) * explosion.damage);
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
                                entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                            }
                        }
                    }
                }
            }

            if (explosion.instigator instanceof Player || event.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                ObjectArrayList<Pair<ItemStack, BlockPos>> drops = new ObjectArrayList<>();

                float radius = Math.min(explosion.radius, explosion.radius * (0.5F + ((float) explosion.age / explosion.duration)));
                int minX = Mth.floor(explosion.position.x - radius - 1.0F);
                int maxX = Mth.floor(explosion.position.x + radius + 1.0F);
                int minY = Mth.floor(explosion.position.y - radius - 1.0F);
                int maxY = Mth.floor(explosion.position.y + radius + 1.0F);
                int minZ = Mth.floor(explosion.position.z - radius - 1.0F);
                int maxZ = Mth.floor(explosion.position.z + radius + 1.0F);

                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            double distance = (x - explosion.position.x) * (x - explosion.position.x) +
                                    (y - explosion.position.y) * (y - explosion.position.y) +
                                    (z - explosion.position.z) * (z - explosion.position.z);

                            if (distance <= radius * radius) {
                                BlockPos pos = new BlockPos(x, y, z);
                                Vec3 center = pos.getCenter();

                                if (!VeilHandler.canDestroy(explosion.instigator, event.level, center.x, center.y, center.z)) {
                                    continue;
                                }

                                BlockState block = event.level.getBlockState(pos);
                                FluidState fluid = event.level.getFluidState(pos);

                                float f = explosion.radius * 2.0F * (0.7F + HelperMethods.RANDOM.nextFloat() * 0.6F);

                                Optional<Float> optional = explosion.calculator.getBlockExplosionResistance(current, event.level, pos, block, fluid);

                                if (optional.isPresent()) {
                                    f -= (optional.get() + 0.3F) * 0.3F;
                                }

                                if (f > 0.0F && explosion.calculator.shouldBlockExplode(current, event.level, pos, block, f)) {
                                    if (!block.isAir()) {
                                        BlockPos imm = pos.immutable();

                                        if (block.canDropFromExplosion(event.level, pos, current)) {
                                            BlockEntity be = block.hasBlockEntity() ? event.level.getBlockEntity(pos) : null;

                                            LootParams.Builder params = (new LootParams.Builder((ServerLevel) event.level))
                                                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                                                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                                                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, be)
                                                    .withOptionalParameter(LootContextParams.THIS_ENTITY, explosion.instigator)
                                                    .withParameter(LootContextParams.EXPLOSION_RADIUS, explosion.radius);
                                            block.spawnAfterBreak((ServerLevel) event.level, pos, ItemStack.EMPTY, explosion.instigator instanceof Player);
                                            block.getDrops(params).forEach(stack -> addBlockDrops(drops, stack, imm));
                                        }
                                        block.onBlockExploded(event.level, pos, current);

                                        if (HelperMethods.RANDOM.nextInt(10) == 0) {
                                            ParticleUtil.sendParticles((ServerLevel) event.level, ParticleTypes.EXPLOSION, true, x, y, z,
                                                    0.0D, 0.0D, 0.0D);
                                        }

                                        if (explosion.fire) {
                                            if (HelperMethods.RANDOM.nextInt(3) == 0 && event.level.getBlockState(pos).isAir() &&
                                                    event.level.getBlockState(pos.below()).isSolidRender(event.level, pos.below())) {
                                                event.level.setBlockAndUpdate(pos, BaseFireBlock.getState(event.level, pos));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                for (Pair<ItemStack, BlockPos> pair : drops) {
                    Block.popResource(event.level, pair.getSecond(), pair.getFirst());
                }
            }
            explosion.age++;

            if (explosion.age >= explosion.duration) {
                remove.add(explosion);
            }
        }
        explosions.removeAll(remove);
    }

    public static void spawn(ResourceKey<Level> dimension, Vec3 position, float radius, int duration, @Nullable LivingEntity instigator, DamageSource source, boolean causesFire) {
        explosions.add(new ExplosionData(dimension, position, radius, duration, 1.0F, instigator, source, causesFire));
    }

    public static void spawn(ResourceKey<Level> dimension, Vec3 position, float radius, int duration, float damage, @Nullable LivingEntity instigator, DamageSource source, boolean causesFire) {
        explosions.add(new ExplosionData(dimension, position, radius, duration, damage, instigator, source, causesFire));
    }

    private static class ExplosionData {
        private final ExplosionDamageCalculator calculator;
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
            this.calculator = instigator == null ? new ExplosionDamageCalculator() : new EntityBasedExplosionDamageCalculator(instigator);
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