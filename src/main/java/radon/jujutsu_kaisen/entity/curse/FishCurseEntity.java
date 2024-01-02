package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.PackCursedSpirit;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class FishCurseEntity extends PackCursedSpirit {
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");

    public FishCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);

        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public FishCurseEntity(FishCurseEntity leader) {
        this(JJKEntities.FISH_CURSE.get(), leader.level());

        this.setLeader(leader);
    }

    @Override
    protected int getMinCount() {
        return 4;
    }

    @Override
    protected int getMaxCount() {
        return 16;
    }

    @Override
    protected PackCursedSpirit spawn() {
        return new FishCurseEntity(this);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean canFly() {
        return true;
    }

    @Override
    public boolean canPerformSorcery() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.FLYING_SPEED);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.GRADE_4.getRequiredExperience();
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    private PlayState walkPredicate(AnimationState<FishCurseEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(SWIM);
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk", this::walkPredicate));
    }
}
