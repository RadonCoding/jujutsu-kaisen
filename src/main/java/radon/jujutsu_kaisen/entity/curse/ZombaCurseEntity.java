package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class ZombaCurseEntity extends CursedSpirit {
    private static final double ATTACK_RANGE = 32.0D;

    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    public ZombaCurseEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
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
        return false;
    }

    @Override
    protected boolean hasMeleeAttack() {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        LivingEntity target = this.getTarget();

        if (target != null) {
            if (this.distanceTo(target) <= ATTACK_RANGE) {
                AbilityHandler.trigger(this, JJKAbilities.SKY_STRIKE.get());
            } else {
                AbilityHandler.trigger(this, JJKAbilities.TELEPORT_TOWARDS.get());
            }
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if (!this.level().isClientSide && pSource.getEntity() instanceof LivingEntity) {
            AbilityHandler.trigger(this, JJKAbilities.TELEPORT_RANDOM.get());
        }
        return super.hurt(pSource, pAmount);
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
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.SKY_STRIKE.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    private PlayState swingPredicate(AnimationState<ZombaCurseEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Idle", animationState -> animationState.setAndContinue(IDLE)));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
    }
}
