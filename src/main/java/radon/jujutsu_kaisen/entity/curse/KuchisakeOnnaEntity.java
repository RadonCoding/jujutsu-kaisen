package radon.jujutsu_kaisen.entity.curse;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.*;

import java.util.Set;

public class KuchisakeOnnaEntity extends CursedSpirit {
    public static final double RANGE = 16.0D;

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    public static EntityDataAccessor<Boolean> DATA_OPEN = SynchedEntityData.defineId(KuchisakeOnnaEntity.class, EntityDataSerializers.BOOLEAN);

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

    private PlayState swingPredicate(AnimationState<KuchisakeOnnaEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk", this::walkPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_OPEN, false);
    }

    public boolean isOpen() {
        return this.entityData.get(DATA_OPEN);
    }

    public void setOpen(boolean open) {
        this.entityData.set(DATA_OPEN, open);
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SEMI_GRADE_1.getRequiredExperience();
    }

    @Override
    @Nullable
    public CursedTechnique getTechnique() {
        return JJKCursedTechniques.SCISSORS.get();
    }

    @Override
    public Set<Ability> getUnlocked() {
        return Set.of(JJKAbilities.SIMPLE_DOMAIN.get());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        this.setOpen(this.getTarget() != null);
    }
}
