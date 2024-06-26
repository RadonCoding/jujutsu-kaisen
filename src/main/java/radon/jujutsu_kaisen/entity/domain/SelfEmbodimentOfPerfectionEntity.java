package radon.jujutsu_kaisen.entity.domain;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import software.bernie.geckolib.animation.*;

public class SelfEmbodimentOfPerfectionEntity extends DomainExpansionCenterEntity {
    private static final RawAnimation ACTIVATE = RawAnimation.begin().thenPlay("misc.activate");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("misc.idle");

    // Seconds to ticks
    private static final int ACTIVATION_DURATION = (int) (2.25F * 20);

    public SelfEmbodimentOfPerfectionEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public SelfEmbodimentOfPerfectionEntity(DomainExpansionEntity domain) {
        super(JJKEntities.SELF_EMBODIMENT_OF_PERFECTION.get(), domain);
    }

    private PlayState activateIdlePredicate(AnimationState<SelfEmbodimentOfPerfectionEntity> animationState) {
        return animationState.setAndContinue(this.getTime() <= ACTIVATION_DURATION ? ACTIVATE : IDLE);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Activate/Idle", this::activateIdlePredicate));
    }
}
