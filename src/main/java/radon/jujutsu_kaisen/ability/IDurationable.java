package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;

public interface IDurationable {
    default int getDuration() {
        return 0;
    }

    default int getRealDuration(LivingEntity owner) {
        int duration = this.getDuration();

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData data = cap.getSorcererData();

        if (duration > 0) {
            duration = (int) (duration * data.getAbilityOutput());
        }
        return duration;
    }
}