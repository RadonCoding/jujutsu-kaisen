package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAdditionalAdaptation;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.tags.JJKEntityTypeTags;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.EntityUtil;

import java.util.*;

public class Infinity extends Ability implements IToggled, IChanneled, IDurationable, IAdditionalAdaptation {
    private static final double SLOWING_FACTOR = 0.0001D;
    private static final double RANGE = 3.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return true;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return ActivationType.CHANNELED;

        ISorcererData data = cap.getSorcererData();
        
        return data.hasTrait(Trait.SIX_EYES) ? ActivationType.TOGGLED : ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.4F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return this.getActivationType(owner) == ActivationType.CHANNELED ? MenuType.MELEE : MenuType.RADIAL;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return true;
    }

    @Override
    public int getAdditional() {
        return 1;
    }

    public static class FrozenProjectileData extends SavedData {
        private static final SavedData.Factory<FrozenProjectileData> FACTORY = new SavedData.Factory<>(FrozenProjectileData::new, FrozenProjectileData::new, null);

        public static final String IDENTIFIER = "frozen_projectile_data";

        private final Map<UUID, FrozenProjectileNBT> frozen;

        public FrozenProjectileData() {
            this.frozen = new HashMap<>();
        }

        public FrozenProjectileData(CompoundTag nbt) {
            this();

            ListTag frozenTag = nbt.getList("frozen", Tag.TAG_COMPOUND);

            for (Tag tag : frozenTag) {
                FrozenProjectileNBT frozen = new FrozenProjectileNBT((CompoundTag) tag);
                this.frozen.put(frozen.getTarget(), frozen);
            }
        }

        @Override
        public @NotNull CompoundTag save(CompoundTag pCompoundTag) {
            ListTag frozenTag = new ListTag();
            frozenTag.addAll(this.frozen.values());
            pCompoundTag.put("frozen", frozenTag);
            return pCompoundTag;
        }

        public void add(LivingEntity source, Entity target) {
            if (!this.frozen.containsKey(target.getUUID())) {
                this.frozen.put(target.getUUID(), new FrozenProjectileNBT(source, target));
                this.setDirty();
            }
        }

        public void tick(ServerLevel level) {
            Iterator<FrozenProjectileNBT> iter = this.frozen.values().iterator();

            while (iter.hasNext()) {
                FrozenProjectileNBT nbt = iter.next();

                Entity target = level.getEntity(nbt.getTarget());

                if (!(level.getEntity(nbt.getSource()) instanceof LivingEntity owner) || owner.isRemoved() || owner.isDeadOrDying()) {
                    if (target != null) {
                        target.setDeltaMovement(nbt.getMovement());
                        target.hurtMarked = true;
                        target.setNoGravity(nbt.isNoGravity());
                    }
                    iter.remove();
                    this.setDirty();
                    continue;
                }

                IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                IAbilityData data = cap.getAbilityData();

                if (target == null) {
                    iter.remove();
                    this.setDirty();
                } else {
                    Vec3 forward = target.getLookAngle();

                    Vec3 start = owner.position().add(forward.scale(owner.getBbWidth() / 2.0F));
                    Vec3 end = target.position().add(forward.scale(-target.getBbWidth() / 2.0F));
                    float dx = (float) (start.x - end.x);
                    float dz = (float) (start.z - end.z);
                    float distance = (float) Math.sqrt(dx * dx + dz * dz);

                    if (data.hasActive(JJKAbilities.INFINITY.get()) && distance <= RANGE) {
                        Vec3 original = nbt.getMovement();
                        target.setDeltaMovement(original.scale(Math.min(SLOWING_FACTOR, distance * SLOWING_FACTOR)));
                        target.hurtMarked = true;
                        target.setNoGravity(true);
                    } else {
                        target.setDeltaMovement(nbt.getMovement());
                        target.hurtMarked = true;
                        target.setNoGravity(nbt.isNoGravity());
                        iter.remove();
                        this.setDirty();
                    }
                }
            }
        }

        private static class FrozenProjectileNBT extends CompoundTag {
            public FrozenProjectileNBT(LivingEntity source, Entity target) {
                this.putUUID("source", source.getUUID());
                this.putUUID("target", target.getUUID());

                this.putBoolean("no_gravity", target.isNoGravity());

                Vec3 movement = target.getDeltaMovement();
                this.putDouble("movement_x", movement.x);
                this.putDouble("movement_y", movement.y);
                this.putDouble("movement_z", movement.z);
            }

            public FrozenProjectileNBT(CompoundTag nbt) {
                this.putUUID("source", nbt.getUUID("source"));
                this.putUUID("target", nbt.getUUID("target"));

                this.putBoolean("no_gravity", nbt.getBoolean("no_gravity"));

                this.putDouble("movement_x", nbt.getDouble("movement_x"));
                this.putDouble("movement_y", nbt.getDouble("movement_y"));
                this.putDouble("movement_z", nbt.getDouble("movement_z"));
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
            if (event.phase == TickEvent.Phase.START) return;

            if (event.level instanceof ServerLevel level) {
                FrozenProjectileData data = level.getDataStorage().computeIfAbsent(FrozenProjectileData.FACTORY, FrozenProjectileData.IDENTIFIER);
                data.tick(level);
            }
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (!(event.getRayTraceResult() instanceof EntityHitResult hit)) return;
            if (!(hit.getEntity() instanceof LivingEntity owner)) return;
            if (!(owner.level() instanceof ServerLevel level)) return;

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasActive(JJKAbilities.INFINITY.get())) return;

            Projectile projectile = event.getProjectile();

            if (!DamageUtil.isBlockable(owner, projectile)) return;

            FrozenProjectileData storage = level.getDataStorage().computeIfAbsent(FrozenProjectileData.FACTORY, FrozenProjectileData.IDENTIFIER);

            storage.add(owner, projectile);

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity owner = event.getEntity();

            if (!(owner.level() instanceof ServerLevel level)) return;

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasActive(JJKAbilities.INFINITY.get())) return;

            FrozenProjectileData storage = level.getDataStorage().computeIfAbsent(FrozenProjectileData.FACTORY, FrozenProjectileData.IDENTIFIER);

            for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, owner.level(), owner, owner.getBoundingBox().inflate(RANGE))) {
                if (entity.getDeltaMovement().lengthSqr() <= 1.0E-7D) continue;
                if (entity instanceof Projectile projectile && !DamageUtil.isBlockable(owner, projectile)) continue;

                storage.add(owner, entity);
            }
        }

        // Has fire before WeaponEventHandler::onLivingAttackLow
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (victimCap == null) return;

            IAbilityData victimData = victimCap.getAbilityData();

            if (!victimData.hasActive(JJKAbilities.INFINITY.get())) return;

            DamageSource source = event.getSource();

            if (!DamageUtil.isBlockable(victim, source)) return;

            event.setCanceled(true);

            // We don't want to play the sound in-case it's a stopped projectile
            if (!(source.getDirectEntity() instanceof Projectile)) {
                if (source.getEntity() instanceof LivingEntity attacker) {
                    IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

                    if (attackerCap != null) {
                        IAbilityData attackerData = attackerCap.getAbilityData();

                        if (attackerData.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                            victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                            return;
                        }
                    }
                }
                victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.MASTER, 1.0F, 1.0F);
            }
        }
    }
}