package radon.jujutsu_kaisen.ability;


import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

import java.util.ArrayList;

public abstract class Transformation extends Ability implements IToggled, ITransformation {
    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (this.getBodyPart() == Part.RIGHT_ARM && !owner.getMainHandItem().isEmpty() || this.getBodyPart() == Part.LEFT_ARM && !owner.getOffhandItem().isEmpty()) {
            return Status.FAILURE;
        }

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Status.FAILURE;

        IAbilityData data = cap.getAbilityData();

        for (Ability ability : new ArrayList<>(data.getToggled())) {
            if (!(ability instanceof ITransformation transformation) || ability == this) continue;

            if (transformation.getBodyPart() == this.getBodyPart()) {
                data.toggle(ability);
            }
        }
        return super.isTriggerable(owner);
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        if (this.getBodyPart() == Part.RIGHT_ARM && !owner.getMainHandItem().isEmpty() || this.getBodyPart() == Part.LEFT_ARM && !owner.getOffhandItem().isEmpty()) {
            return Status.FAILURE;
        }
        return super.isStillUsable(owner);
    }
}
