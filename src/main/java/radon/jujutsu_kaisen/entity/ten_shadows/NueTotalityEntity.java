package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class NueTotalityEntity extends TenShadowsSummon {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
    private static final RawAnimation SHOCK = RawAnimation.begin().thenPlay("attack.shock");

    public NueTotalityEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public NueTotalityEntity(LivingEntity owner) {
        this(JJKEntities.NUE_TOTALITY.get(), owner.level);

        this.setTame(true);
        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(HelperMethods.getLookAngle(owner).multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void customServerAiStep() {
        LivingEntity target = this.getTarget();

        if (target != null && !target.isRemoved() && target.isAlive()) {
            this.setOrderedToSit(true);

            if (this.getY() >= target.getY() + (this.getBbHeight() * 3.0F) && Math.sqrt(this.distanceToSqr(target.getX(), this.getY(), target.getZ())) <= 1.0D) {
                if (this.random.nextInt(5) != 0) return;

                if (AbilityHandler.trigger(this, JJKAbilities.NUE_TOTALITY_LIGHTNING.get()) == Ability.Status.SUCCESS) {
                    this.swing(InteractionHand.MAIN_HAND);
                }
            } else {
                this.moveControl.setWantedPosition(target.getX(), target.getY() + (this.getBbHeight() * 3.0F), target.getZ(), this.getFlyingSpeed());
            }
        } else {
            this.setOrderedToSit(false);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 25.0F, 12.5F, true));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        if (super.doHurtTarget(pEntity)) {
            if (pEntity instanceof LivingEntity living) {
                Ability lightning = JJKAbilities.NUE_LIGHTNING.get();
                ((Ability.ITenShadowsAttack) lightning).perform(this, living);
            }
            return true;
        }
        return false;
    }

    @Override
    protected float getFlyingSpeed() {
        return 0.3F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.MAX_HEALTH, 4 * 20.0D);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    private PlayState flyIdlePredicate(AnimationState<NueTotalityEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(FLY);
        } else {
            return animationState.setAndContinue(IDLE);
        }
    }

    private PlayState shockPredicate(AnimationState<NueTotalityEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SHOCK);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Fly/Idle", this::flyIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Shock", this::shockPredicate));
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.NUE_TOTALITY.get();
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.NUE_TOTALITY_LIGHTNING.get());
    }
}
