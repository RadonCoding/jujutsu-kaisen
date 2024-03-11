package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class KuchisakeOnnaEntity extends CursedSpirit {
    public static final double RANGE = 16.0D;
    private static final int SNIP_DURATION = 5;
    private static final float DAMAGE = 5.0F;
    private static final int INTERVAL = 3 * 20;

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SNIP = RawAnimation.begin().thenPlay("attack.snip");

    public static EntityDataAccessor<Optional<UUID>> DATA_TARGET = SynchedEntityData.defineId(KuchisakeOnnaEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    public static EntityDataAccessor<Boolean> DATA_OPEN = SynchedEntityData.defineId(KuchisakeOnnaEntity.class, EntityDataSerializers.BOOLEAN);
    public static EntityDataAccessor<Integer> DATA_SNIP = SynchedEntityData.defineId(KuchisakeOnnaEntity.class, EntityDataSerializers.INT);

    private Vec3 start;
    private int cooldown;

    public KuchisakeOnnaEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public boolean canChant() {
        return false;
    }

    @Override
    public boolean hasMeleeAttack() {
        return false;
    }

    @Override
    public boolean hasArms() {
        return false;
    }

    @Override
    public boolean canJump() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.165D);
    }

    private PlayState walkPredicate(AnimationState<KuchisakeOnnaEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(WALK);
        }
        return PlayState.STOP;
    }

    private PlayState cutPredicate(AnimationState<KuchisakeOnnaEntity> animationState) {
        if (this.entityData.get(DATA_SNIP) > 0) {
            return animationState.setAndContinue(SNIP);
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk", this::walkPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Snip", this::cutPredicate));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_TARGET, Optional.empty());
        this.entityData.define(DATA_OPEN, false);
        this.entityData.define(DATA_SNIP, 0);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        Entity source = pSource.getEntity();

        if (source != null) {
            Optional<UUID> identifier = this.entityData.get(DATA_TARGET);

            if (identifier.isPresent()) {
                if (source.getUUID() == identifier.get()) {
                    return false;
                }
            }
        }
        return super.hurt(pSource, pAmount);
    }

    public boolean isOpen() {
        return this.entityData.get(DATA_OPEN);
    }

    public Optional<UUID> getCurrent() {
        return this.entityData.get(DATA_TARGET);
    }

    public void reset() {
        this.entityData.set(DATA_OPEN, false);
        this.entityData.set(DATA_TARGET, Optional.empty());

        IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(JJKAbilities.SCISSORS.get())) {
            AbilityHandler.trigger(this, JJKAbilities.SCISSORS.get());
        }
        this.start = null;
        this.cooldown = INTERVAL;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.SCISSORS.get());
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return null;
    }

    @Override
    public Set<Ability> getUnlocked() {
        return Set.of(JJKAbilities.SIMPLE_DOMAIN.get());
    }

    public void attack() {
        this.entityData.set(DATA_SNIP, SNIP_DURATION);
        AbilityHandler.trigger(this, JJKAbilities.SCISSORS.get());

        this.getCurrent().ifPresent(identifier -> {
            Entity target = ((ServerLevel) this.level()).getEntity(identifier);

            if (target == null) return;

            IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            target.hurt(JJKDamageSources.jujutsuAttack(this, null), DAMAGE * data.getAbilityOutput());
        });
        this.reset();
    }

    @Override
    protected void customServerAiStep() {
        if (this.level().isClientSide) return;

        int snip = this.entityData.get(DATA_SNIP);

        if (snip > 0) {
            this.entityData.set(DATA_SNIP, --snip);
        }

        if (this.cooldown > 0) this.cooldown--;

        if (!VeilHandler.getDomains(((ServerLevel) this.level()), this.blockPosition()).isEmpty()) {
            this.reset();
            return;
        }

        this.getCurrent().ifPresent(identifier -> {
            if (!(((ServerLevel) this.level()).getEntity(identifier) instanceof LivingEntity target)) return;

            IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            this.moveControl.setWantedPosition(this.getX(), this.getY(), this.getZ(), this.getSpeed());

            if (data.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get()) || this.distanceTo(target) > RANGE) {
                this.reset();
            } else if (Math.sqrt(target.distanceToSqr(this.start)) >= 3.0D) {
                this.attack();
            }
        });

        LivingEntity target = this.getTarget();

        if (target != null) {
            IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                IAbilityData data = cap.getAbilityData();

                if (data.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) return;
            }
        }

        if (target == null || target.isRemoved() || !target.isAlive()) {
            return;
        }

        if (!this.isOpen()) {
            if (this.cooldown > 0) return;

            if (this.distanceTo(target) <= RANGE) {
                this.entityData.set(DATA_TARGET, Optional.of(target.getUUID()));

                this.start = target.position();

                if (JJKAbilities.SCISSORS.get().getStatus(this) == Ability.Status.SUCCESS) {
                    target.sendSystemMessage(Component.translatable(String.format("chat.%s.kuchisake_onna", JujutsuKaisen.MOD_ID), this.getName().getString()));
                    this.entityData.set(DATA_OPEN, true);
                } else {
                    this.entityData.set(DATA_TARGET, Optional.empty());
                }
            } else {
                this.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), this.getSpeed());
            }
        }
    }
}
