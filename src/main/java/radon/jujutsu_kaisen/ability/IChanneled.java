package radon.jujutsu_kaisen.ability;


import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

public interface IChanneled {
    default void onStop(LivingEntity owner) {
    }

    default int getCharge(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        IAbilityData data = cap.getAbilityData();
        return data.getCharge();
    }
}
