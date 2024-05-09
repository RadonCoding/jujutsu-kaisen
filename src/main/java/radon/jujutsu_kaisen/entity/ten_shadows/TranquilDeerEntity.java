package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.*;

import java.util.Set;

public class TranquilDeerEntity extends TenShadowsSummon {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    public TranquilDeerEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public TranquilDeerEntity(LivingEntity owner, boolean tame) {
        this(JJKEntities.TRANQUIL_DEER.get(), owner.level());

        this.setTame(tame, false);
        this.setOwner(owner);

        Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
        Vec3 pos = owner.position()
                .subtract(direction.multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setPathfindingMalus(PathType.LEAVES, 0.0F);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean canFly() {
        return false;
    }

    @Override
    public boolean canChant() {
        return false;
    }

    @Override
    public boolean hasMeleeAttack() {
        return true;
    }

    @Override
    public boolean hasArms() {
        return false;
    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (pPlayer == this.getOwner() && this.isTame()) {
            this.lookAt(EntityAnchorArgument.Anchor.EYES, pPlayer.position().add(0.0D, pPlayer.getBbHeight() / 2.0F, 0.0D));

            this.setTarget(null);

            if (AbilityHandler.trigger(this, JJKAbilities.OUTPUT_RCT.get()) == Ability.Status.SUCCESS) {
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return InteractionResult.FAIL;
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 3 * 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3 * 2.0D)
                .add(Attributes.STEP_HEIGHT, 2.0F);
    }

    private PlayState walkRunIdlePredicate(AnimationState<TranquilDeerEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else {
            return animationState.setAndContinue(IDLE);
        }
    }

    private PlayState swingPredicate(AnimationState<TranquilDeerEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run/Idle", this::walkRunIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", 2, this::swingPredicate));
    }

    @Override
    public float getExperience() {
        return this.isTame() ? super.getExperience() : SorcererGrade.GRADE_1.getRequiredExperience();
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.TRANQUIL_DEER.get();
    }

    @Override
    public Set<Ability> getUnlocked() {
        return Set.of(JJKAbilities.RCT1.get(), JJKAbilities.OUTPUT_RCT.get());
    }
}
