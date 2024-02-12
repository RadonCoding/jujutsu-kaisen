package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;

import java.util.ArrayList;

public abstract class Transformation extends Ability implements Ability.IToggled, ITransformation {
    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (this.getBodyPart() == Part.RIGHT_ARM && !owner.getMainHandItem().isEmpty() || this.getBodyPart() == Part.LEFT_ARM && !owner.getOffhandItem().isEmpty()) {
            return Status.FAILURE;
        }

        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);

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
