package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

import java.util.ArrayList;

public abstract class Transformation extends Ability implements Ability.IToggled, ITransformation {
    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (this.getBodyPart() == Part.RIGHT_ARM && !owner.getMainHandItem().isEmpty() || this.getBodyPart() == Part.LEFT_ARM && !owner.getOffhandItem().isEmpty()) {
            return Status.FAILURE;
        }

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (Ability ability : new ArrayList<>(cap.getToggled())) {
            if (!(ability instanceof ITransformation transformation) || ability == this) continue;

            if (transformation.getBodyPart() == this.getBodyPart()) {
                cap.toggle(ability);
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
