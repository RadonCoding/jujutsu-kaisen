package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.ai.goal.LookAtTargetGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableSorcererGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class CyclopsCurseEntity extends SorcererEntity {
    public static EntityDataAccessor<Integer> DATA_SMASH = SynchedEntityData.defineId(CyclopsCurseEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation SMASH = RawAnimation.begin().thenPlay("attack.smash");

    private static final int SMASH_DURATION = 2 * 20;

    public CyclopsCurseEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_SMASH, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new SorcererGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableSorcererGoal(this, false));
    }

    private PlayState walkPredicate(AnimationState<CyclopsCurseEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(WALK);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<CyclopsCurseEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState smashPredicate(AnimationState<CyclopsCurseEntity> animationState) {
        int slash = this.entityData.get(DATA_SMASH);

        if (slash > 0) {
            return animationState.setAndContinue(SMASH);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk", this::walkPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Smash", this::smashPredicate));
    }

    @Override
    protected void customServerAiStep() {
        LivingEntity target = this.getTarget();

        int smash = this.entityData.get(DATA_SMASH);

        if (smash > 0) {
            if (smash == SMASH_DURATION / 2) {
                AbilityHandler.trigger(this, JJKAbilities.CYCLOPS_SMASH.get());
            }
            this.entityData.set(DATA_SMASH, --smash);
        } else {
            if (target != null) {
                if (this.isOnGround() && this.distanceTo(target) < 3.0D) {
                    this.entityData.set(DATA_SMASH, SMASH_DURATION);
                }
            }
        }
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.CYCLOPS_SMASH.get());
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_1;
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
    public boolean isCurse() {
        return true;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }
}
