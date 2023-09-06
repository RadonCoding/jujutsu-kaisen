package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnna;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainItemProjectile;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.*;

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
        if (owner instanceof Player player) {
            if (player.getAbilities().instabuild) return;

            if (player.getAbilities().flying) {
                player.getAbilities().setFlyingSpeed(0.025F);
            }
        }
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.4F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        if (owner instanceof Player player) {
            if (player.getAbilities().instabuild) return;
            player.getAbilities().mayfly = true;
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (owner instanceof Player player) {
            if (player.getAbilities().instabuild) return;
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }
    }

    @Override
    public Classification getClassification() {
        return Classification.LIMITLESS;
    }

    @Override
    public int getCooldown() {
        return 20;
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
                    boolean result = false;

                    if (source != null && source.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                        ISorcererData cap = source.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                        result = !cap.hasToggled(JJKAbilities.INFINITY.get()) || source.distanceTo(target) >= 2.5F;
                    }

                    if (source == null || result) {
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

    private static boolean canBlock(LivingEntity target, Projectile projectile) {
        if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (projectile instanceof ThrownChainItemProjectile chain) {
            if (chain.getStack().is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) return false;
        }
        for (DomainExpansionEntity domain : cap.getDomains(((ServerLevel) target.level))) {
            if (projectile.getOwner() == domain.getOwner()) return false;
        }
        return !(projectile instanceof ScissorEntity);
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
            if (!(event.getRayTraceResult() instanceof EntityHitResult hit)) return;
            if (!(hit.getEntity() instanceof LivingEntity target)) return;
            if (!(target.level instanceof ServerLevel level)) return;

            FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load,
                    FrozenProjectileData::new, FrozenProjectileData.IDENTIFIER);

            if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.hasToggled(JJKAbilities.INFINITY.get())) {
                Projectile projectile = event.getProjectile();

                if (!Infinity.canBlock(target, projectile)) return;

                data.add(target, projectile);
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity target = event.getEntity();

            if (!(target.level instanceof ServerLevel level)) return;

            FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData::load,
                    FrozenProjectileData::new, FrozenProjectileData.IDENTIFIER);

            if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.hasToggled(JJKAbilities.INFINITY.get())) {
                for (Projectile projectile : target.level.getEntitiesOfClass(Projectile.class, target.getBoundingBox().inflate(1.0D))) {
                    if (!Infinity.canBlock(target, projectile)) continue;
                    data.add(target, projectile);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity target = event.getEntity();

            target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(targetCap -> {
                if (targetCap.hasToggled(JJKAbilities.INFINITY.get())) {
                    DamageSource source = event.getSource();

                    if (!source.is(JJKDamageSources.SOUL) && source.is(DamageTypeTags.BYPASSES_ARMOR)) {
                        return;
                    }

                    if (target.level instanceof ServerLevel level) {
                        for (DomainExpansionEntity domain : targetCap.getDomains(level)) {
                            Entity owner = domain.getOwner();

                            if (owner == source.getEntity()) {
                                return;
                            }
                        }

                        for (KuchisakeOnna curse : target.level.getEntitiesOfClass(KuchisakeOnna.class, AABB.ofSize(target.position(),
                                KuchisakeOnna.RANGE, KuchisakeOnna.RANGE, KuchisakeOnna.RANGE))) {
                            Optional<UUID> identifier = curse.getCurrent();
                            if (identifier.isEmpty()) continue;
                            if (identifier.get().equals(target.getUUID())) return;
                        }
                    }

                    if (source.getEntity() instanceof LivingEntity living) {
                        if (source.getDirectEntity() instanceof ScissorEntity) return;

                        if (living.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                            ISorcererData cap = living.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                            if (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource src && src.getAbility() != null &&
                                    src.getAbility().getClassification() == Classification.MELEE && cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                                return;
                            }

                            boolean melee = source.getDirectEntity() == source.getEntity() &&
                                    (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK));

                            if (melee) {
                                if (cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                                    return;
                                }
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