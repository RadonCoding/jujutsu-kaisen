package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class AgitoEntity extends TenShadowsSummon {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    public AgitoEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
    public boolean canPerformSorcery() {
        return true;
    }

    @Override
    protected boolean hasMeleeAttack() {
        return true;
    }

    public AgitoEntity(LivingEntity owner) {
        this(JJKEntities.AGITO.get(), owner.level());

        this.setTame(true);
        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle()
                        .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (pPlayer == this.getOwner() && this.isTame() && !this.isVehicle()) {
            this.yHeadRot = HelperMethods.getYRotD(this, pPlayer.getEyePosition());
            this.yBodyRot = HelperMethods.getYRotD(this, pPlayer.getEyePosition());

            this.setXRot(HelperMethods.getXRotD(this, pPlayer.getEyePosition()));
            this.setYRot(HelperMethods.getYRotD(this, pPlayer.getEyePosition()));

            if (AbilityHandler.trigger(this, JJKAbilities.HEAL_RCT.get()) == Ability.Status.SUCCESS) {
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return InteractionResult.FAIL;
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public float getStepHeight() {
        return 2.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 5 * 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 5 * 2.0D);
    }

    private PlayState walkRunIdlePredicate(AnimationState<AgitoEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else {
            return animationState.setAndContinue(IDLE);
        }
    }

    private PlayState swingPredicate(AnimationState<AgitoEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run/Idle", this::walkRunIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
    }

    @Override
    protected void customServerAiStep() {
        this.setSprinting(this.getDeltaMovement().lengthSqr() > 0.01D && this.moveControl.getSpeedModifier() > 1.0D);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (this.isTame()) {
            LivingEntity owner = this.getOwner();

            if (owner != null) {
                this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(srcCap -> {
                    owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(dstCap -> {
                        dstCap.addAdapted(srcCap.getAdapted());
                    });
                });
            }
        }
    }

    private void breakBlocks() {
        AABB bounds = this.getBoundingBox();

        BlockPos.betweenClosedStream(bounds).forEach(pos -> {
            BlockState state = this.level().getBlockState(pos);

            if (state.getFluidState().isEmpty() && state.canOcclude() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                this.level().destroyBlock(pos, false);
            }
        });
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                this.breakBlocks();
            }
        }
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.AGITO.get();
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.NUE_LIGHTNING.get(), JJKAbilities.HEAL_RCT.get());
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE);
    }
}
