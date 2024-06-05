package radon.jujutsu_kaisen.entity.curse;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.entity.curse.base.DisasterCurse;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.*;

import java.util.Set;

public class HanamiEntity extends DisasterCurse {
    private static final EntityDataAccessor<Boolean> DATA_CAST = SynchedEntityData.defineId(HanamiEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    public HanamiEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_CAST, true);
    }

    public boolean hasCast() {
        return this.entityData.get(DATA_CAST);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public boolean canChant() {
        return true;
    }

    @Override
    @Nullable
    public CursedTechnique getTechnique() {
        return JJKCursedTechniques.DISASTER_PLANTS.get();
    }

    @Override
    public Set<Ability> getUnlocked() {
        return Set.of(JJKAbilities.SHINING_SEA_OF_FLOWERS.get(), JJKAbilities.DOMAIN_AMPLIFICATION.get());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        LivingEntity target = this.getTarget();

        boolean wear = false;

        if (target == null) {
            wear = true;
        } else {
            if (target.getMaxHealth() / this.getMaxHealth() >= 2) {
                wear = true;
            }
        }
        this.entityData.set(DATA_CAST, wear);
    }


    private PlayState walkRunPredicate(AnimationState<HanamiEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<HanamiEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run", this::walkRunPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", 2, this::swingPredicate));
    }
}
