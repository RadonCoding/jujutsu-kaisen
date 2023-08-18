package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
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
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainItemProjectile;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Infinity extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return true;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.25F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        /*if (owner instanceof Player player) {
            player.getAbilities().mayfly = true;
        }*/
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        /*if (owner instanceof Player player) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }*/
    }

    @Override
    public Classification getClassification() {
        return Classification.LIMITLESS;
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
            if (target.getOwner() != source && !this.frozen.containsKey(target.getUUID())) {
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
                } else {
                    AtomicBoolean result = new AtomicBoolean();

                    if (source == null) {
                        result.set(true);
                    } else {
                        source.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                            if (!cap.hasToggled(JJKAbilities.INFINITY.get()) || source.distanceTo(target) >= 2.5F) {
                                result.set(true);
                            }
                        });
                    }

                    if (result.get()) {
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

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLevelTick(TickEvent.LevelTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;

            if (event.level instanceof ServerLevel level) {
                FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load,
                        FrozenProjectileData::new, FrozenProjectileData.IDENTIFIER);
                data.tick(level);
            }
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (event.getRayTraceResult() instanceof EntityHitResult result) {
                if (result.getEntity() instanceof LivingEntity target) {
                    if (target.level instanceof ServerLevel level) {
                        FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load,
                                FrozenProjectileData::new, FrozenProjectileData.IDENTIFIER);

                        target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                            Projectile projectile = event.getProjectile();

                            if (projectile instanceof ThrownChainItemProjectile chain) {
                                if (chain.getStack().is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) return;
                            }

                            for (DomainExpansionEntity domain : cap.getDomains(level)) {
                                if (projectile.getOwner() == domain.getOwner()) return;
                            }

                            if (cap.hasToggled(JJKAbilities.INFINITY.get())) {
                                data.add(target, projectile);
                                event.setCanceled(true);
                            }
                        });
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity target = event.getEntity();

            if (target.level instanceof ServerLevel level) {
                FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load,
                        FrozenProjectileData::new, FrozenProjectileData.IDENTIFIER);

                target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    if (cap.hasToggled(JJKAbilities.INFINITY.get())) {
                        for (Projectile projectile : target.level.getEntitiesOfClass(Projectile.class, target.getBoundingBox().inflate(1.0D))) {
                            for (DomainExpansionEntity domain : cap.getDomains(level)) {
                                if (projectile.getOwner() == domain.getOwner()) return;
                            }
                            data.add(target, projectile);
                        }
                    }
                });
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity target = event.getEntity();

            target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(targetCap -> {
                if (targetCap.hasToggled(JJKAbilities.INFINITY.get())) {
                    DamageSource source = event.getSource();

                    if (source.is(JJKDamageSources.JUJUTSU)) {
                        if (target.level instanceof ServerLevel level) {
                            for (DomainExpansionEntity domain : targetCap.getDomains(level)) {
                                Entity owner = domain.getOwner();

                                if (owner == source.getEntity()) {
                                    return;
                                }
                            }
                        }
                    }

                    if (source.is(DamageTypes.OUT_OF_WORLD)) {
                        return;
                    }

                    if (source.getEntity() instanceof LivingEntity living) {
                        if (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource src && src.getAbility() != null &&
                                src.getAbility().getClassification() == Classification.MELEE && JJKAbilities.hasToggled(living, JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                            return;
                        }

                        if (source.getDirectEntity() == source.getEntity() &&
                                (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK))) {
                            if (living.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                                target.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GLASS_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                                return;
                            } else if (JJKAbilities.hasToggled(living, JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                                return;
                            }

                            if (living instanceof MahoragaEntity) {
                                AtomicBoolean result = new AtomicBoolean();

                                living.getCapability(SorcererDataHandler.INSTANCE).ifPresent(attackerCap -> {
                                    if (attackerCap.isAdaptedTo(JJKAbilities.INFINITY.get())) {
                                        result.set(true);
                                    }
                                });

                                if (result.get()) {
                                    return;
                                }
                            }
                        } else if (source.getDirectEntity() instanceof ThrownChainItemProjectile chain) {
                            if (chain.getStack().is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                                target.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GLASS_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                                return;
                            }
                        }
                    }

                    target.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.MASTER, 1.0F, 1.0F);

                    event.setCanceled(true);
                }
            });
        }
    }
}