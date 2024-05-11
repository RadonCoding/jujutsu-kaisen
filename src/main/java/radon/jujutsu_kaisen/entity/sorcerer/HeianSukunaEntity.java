package radon.jujutsu_kaisen.entity.sorcerer;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.item.registry.JJKItems;
import radon.jujutsu_kaisen.item.cursed_object.SukunaFingerItem;
import radon.jujutsu_kaisen.item.cursed_tool.KamutokeDaggerItem;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.*;

import java.util.ArrayList;
import java.util.List;

public class HeianSukunaEntity extends SukunaEntity {
    private static final EntityDataAccessor<Boolean> DATA_IDLE = SynchedEntityData.defineId(HeianSukunaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_BARRAGE = SynchedEntityData.defineId(HeianSukunaEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation BARRAGE = RawAnimation.begin().thenLoop("attack.barrage");
    private static final RawAnimation SIT = RawAnimation.begin().thenPlayAndHold("misc.sit");

    public HeianSukunaEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public HeianSukunaEntity(Level pLevel, int fingers) {
        super(JJKEntities.HEIAN_SUKUNA.get(), pLevel);

        this.fingers = fingers;
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);

        if (this.fingers < 20) return;

        ItemStack stack = new ItemStack(JJKItems.SUKUNA_FINGER.get());
        stack.set(JJKDataComponentTypes.IS_FULL_SOUL, true);
        this.spawnAtLocation(stack);
    }

    public void setBarrage(int barrage) {
        this.entityData.set(DATA_BARRAGE, barrage);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_IDLE, false);
        pBuilder.define(DATA_BARRAGE, 0);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.HITEN_STAFF.get()));
        this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(JJKItems.KAMUTOKE_DAGGER.get()));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        this.entityData.set(DATA_IDLE, this.getTarget() == null && this.getDeltaMovement().lengthSqr() < 9.01D);

        int barrage = this.entityData.get(DATA_BARRAGE);

        if (barrage > 0) {
            this.entityData.set(DATA_BARRAGE, --barrage);
        }

        if (this.random.nextInt(5 * 20) == 0) {
            LivingEntity target = this.getTarget();

            if (target != null) {
                this.startUsingItem(InteractionHand.OFF_HAND);

                if (RotationUtil.getLookAtHit(this, KamutokeDaggerItem.RANGE) instanceof EntityHitResult hit && hit.getEntity() == target) {
                    this.stopUsingItem();
                }
            }
        }
    }

    private PlayState walkRunSitPredicate(AnimationState<HeianSukunaEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else if (this.entityData.get(DATA_IDLE)) {
            return animationState.setAndContinue(SIT);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<HeianSukunaEntity> animationState) {
        if (this.entityData.get(DATA_BARRAGE) > 0) {
            return animationState.setAndContinue(BARRAGE);
        } else if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run/Sit", this::walkRunSitPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", 2, this::swingPredicate));
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        List<Trait> traits = new ArrayList<>(super.getTraits());
        traits.add(Trait.PERFECT_BODY);
        return traits;
    }
}
