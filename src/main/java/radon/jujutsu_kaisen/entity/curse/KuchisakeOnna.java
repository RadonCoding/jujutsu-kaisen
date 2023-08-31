package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.ai.goal.HealingGoal;
import radon.jujutsu_kaisen.entity.ai.goal.LookAtTargetGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableSorcererGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class KuchisakeOnna extends SorcererEntity {
    public static final double RANGE = 16.0D;
    private static final int SNIP_DURATION = 5;
    private static final float DAMAGE = 5.0F;

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SNIP = RawAnimation.begin().thenPlay("attack.snip");

    public static EntityDataAccessor<Optional<UUID>> DATA_TARGET = SynchedEntityData.defineId(KuchisakeOnna.class, EntityDataSerializers.OPTIONAL_UUID);
    public static EntityDataAccessor<Boolean> DATA_OPEN = SynchedEntityData.defineId(KuchisakeOnna.class, EntityDataSerializers.BOOLEAN);
    public static EntityDataAccessor<Integer> DATA_SNIP = SynchedEntityData.defineId(KuchisakeOnna.class, EntityDataSerializers.INT);

    private Vec3 start;

    public KuchisakeOnna(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.17D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(3, new HealingGoal(this));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableSorcererGoal(this, true));
    }

    private PlayState walkPredicate(AnimationState<KuchisakeOnna> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(WALK);
        }
        return PlayState.STOP;
    }

    private PlayState cutPredicate(AnimationState<KuchisakeOnna> animationState) {
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
        AtomicBoolean result = new AtomicBoolean();

        Entity source = pSource.getEntity();

        if (source != null) {
            this.entityData.get(DATA_TARGET).ifPresent(identifier -> {
                if (source.getUUID().equals(identifier)) {
                    result.set(true);
                }
            });
        }
        return !result.get() && super.hurt(pSource, pAmount);
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

        if (JJKAbilities.hasToggled(this, JJKAbilities.SCISSORS.get())) {
            AbilityHandler.trigger(this, JJKAbilities.SCISSORS.get());
        }
        this.start = null;
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.SCISSORS.get());
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of();
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.CURSE;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    public void attack() {
        this.entityData.set(DATA_SNIP, SNIP_DURATION);
        AbilityHandler.trigger(this, JJKAbilities.SCISSORS.get());

        this.getCurrent().ifPresent(identifier -> {
            Entity target = ((ServerLevel) this.level).getEntity(identifier);

            if (target == null) return;

            this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                target.hurt(JJKDamageSources.jujutsuAttack(this, null), DAMAGE * cap.getGrade().getPower()));
        });
        this.reset();
    }

    @Override
    protected void customServerAiStep() {
        if (this.level.isClientSide) return;

        int snip = this.entityData.get(DATA_SNIP);

        if (snip > 0) {
            this.entityData.set(DATA_SNIP, --snip);
        }

        this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getDomains(((ServerLevel) this.level)).size() > 0) {
                this.reset();
                return;
            }

            this.getCurrent().ifPresent(identifier -> {
                Entity target = ((ServerLevel) this.level).getEntity(identifier);

                if (target == null) return;

                this.moveControl.setWantedPosition(this.getX(), this.getY(), this.getZ(), this.getSpeed());

                if (this.distanceTo(target) > RANGE) {
                    this.reset();
                } else if (Math.sqrt(target.distanceToSqr(this.start)) >= 3.0D) {
                    this.attack();
                }
            });

            LivingEntity target = this.getTarget();

            if (target == null || target.isRemoved() || !target.isAlive()) {
                this.reset();
                return;
            }

            if (!this.isOpen()) {
                if (this.distanceTo(target) <= RANGE) {
                    this.entityData.set(DATA_TARGET, Optional.of(target.getUUID()));

                    this.start = target.position();

                    if (JJKAbilities.SCISSORS.get().getStatus(this, true, false, false, false) == Ability.Status.SUCCESS) {
                        target.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 3 * 20, 0, false, false, false));
                        target.sendSystemMessage(Component.translatable(String.format("chat.%s.kuchisake_onna", JujutsuKaisen.MOD_ID), this.getDisplayName()));
                        this.entityData.set(DATA_OPEN, true);
                    } else {
                        this.entityData.set(DATA_TARGET, Optional.empty());
                    }
                }
            }
        });
    }
}
