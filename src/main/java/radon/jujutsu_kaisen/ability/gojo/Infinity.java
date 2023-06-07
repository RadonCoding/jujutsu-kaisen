package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JujutsuAbilities;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Infinity extends Ability implements Ability.IToggled {
    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public void runClient(LivingEntity entity) {

    }

    @Override
    public void runServer(LivingEntity entity) {

    }

    @Override
    public float getCost() {
        return 1.0F;
    }

    public static class FrozenProjectileData extends SavedData {
        public static final String IDENTIFIER = "frozen_projectile_data";

        private final Map<UUID, FrozenProjectileNBT> frozen;

        public FrozenProjectileData() {
            this.frozen = new HashMap<>();
        }

        public static FrozenProjectileData load(CompoundTag pCompoundTag) {
            FrozenProjectileData data = new FrozenProjectileData();
            ListTag frozenTag = pCompoundTag.getList("frozen", Tag.TAG_COMPOUND);

            for (Tag tag : frozenTag) {
                FrozenProjectileNBT nbt = new FrozenProjectileNBT((CompoundTag) tag);
                data.frozen.put(nbt.getTarget(), nbt);
            }
            return data;
        }

        @Override
        public @NotNull CompoundTag save(CompoundTag pCompoundTag) {
            ListTag frozenTag = new ListTag();
            frozenTag.addAll(this.frozen.values());
            pCompoundTag.put("frozen", frozenTag);
            return pCompoundTag;
        }

        public void add(LivingEntity source, Projectile target) {
            if (!this.frozen.containsKey(target.getUUID())) {
                this.frozen.put(target.getUUID(), new FrozenProjectileNBT(source, target));
                this.setDirty();
            }
        }

        public void tick(ServerLevel level) {
            Iterator<FrozenProjectileNBT> iter = this.frozen.values().iterator();

            while (iter.hasNext()) {
                FrozenProjectileNBT nbt = iter.next();

                Entity source = level.getEntity(nbt.getSource());
                Entity target = level.getEntity(nbt.getTarget());

                if (target == null) {
                    iter.remove();
                    this.setDirty();
                } else if (source == null || source.distanceTo(target) >= 2.5F) {
                    target.setNoGravity(nbt.isNoGravity());
                    iter.remove();
                    this.setDirty();
                } else {
                    Vec3 original = nbt.getMovement();
                    target.setDeltaMovement(original.scale(Double.MIN_VALUE));
                    target.setNoGravity(true);
                }
            }
        }

        private static class FrozenProjectileNBT extends CompoundTag {
            public FrozenProjectileNBT(LivingEntity source, Projectile target) {
                this.putUUID("source", source.getUUID());
                this.putUUID("target", target.getUUID());

                this.putBoolean("no_gravity", target.isNoGravity());

                Vec3 movement = target.getDeltaMovement();
                this.putDouble("movement_x", movement.x());
                this.putDouble("movement_y", movement.y());
                this.putDouble("movement_z", movement.z());
            }

            public FrozenProjectileNBT(CompoundTag tag) {
                this.putUUID("source", tag.getUUID("source"));
                this.putUUID("target", tag.getUUID("target"));

                this.putBoolean("no_gravity", tag.getBoolean("no_gravity"));

                this.putDouble("movement_x", tag.getDouble("movement_x"));
                this.putDouble("movement_y", tag.getDouble("movement_y"));
                this.putDouble("movement_z", tag.getDouble("movement_z"));
            }

            public UUID getSource() {
                return this.getUUID("source");
            }

            public UUID getTarget() {
                return this.getUUID("target");
            }

            public boolean isNoGravity() {
                return this.getBoolean("no_gravity");
            }

            public Vec3 getMovement() {
                double x = this.getDouble("movement_x");
                double y = this.getDouble("movement_y");
                double z = this.getDouble("movement_z");
                return new Vec3(x, y, z);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLevelTick(TickEvent.LevelTickEvent event) {
            if (event.level instanceof ServerLevel level) {
                FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load,
                        FrozenProjectileData::new, FrozenProjectileData.IDENTIFIER);
                data.tick(level);
            }
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (event.getRayTraceResult() instanceof EntityHitResult result) {
                if (result.getEntity() instanceof LivingEntity entity) {
                    if (entity.level instanceof ServerLevel level) {
                        FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load,
                                FrozenProjectileData::new, FrozenProjectileData.IDENTIFIER);

                        entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                            if (cap.hasToggled(JujutsuAbilities.INFINITY.get())) {
                                Projectile projectile = event.getProjectile();
                                data.add(entity, projectile);
                                event.setCanceled(true);
                            }
                        });
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity entity = event.getEntity();

            if (entity.level instanceof ServerLevel level) {
                FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load,
                        FrozenProjectileData::new, FrozenProjectileData.IDENTIFIER);

                entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    if (cap.hasToggled(JujutsuAbilities.INFINITY.get())) {
                        for (Projectile projectile : entity.level.getEntitiesOfClass(Projectile.class, entity.getBoundingBox().inflate(1.0D))) {
                            data.add(entity, projectile);
                        }
                    }
                });
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity target = event.getEntity();

            target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                DamageSource source = event.getSource();

                if (source.isBypassInvul()) {
                    return;
                }

                if (cap.hasToggled(JujutsuAbilities.INFINITY.get())) {
                    event.setCanceled(true);
                }
            });
        }
    }
}