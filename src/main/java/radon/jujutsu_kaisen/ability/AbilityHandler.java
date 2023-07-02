package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public class AbilityHandler {
    public static void trigger(LivingEntity owner, Ability ability) {
        if (ability.checkStatus(owner) != Ability.Status.SUCCESS) {
            return;
        }

        if (ability.getActivationType() == Ability.ActivationType.INSTANT) {
            ability.run(owner);
        } else if (ability.getActivationType() == Ability.ActivationType.TOGGLED) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.toggleAbility(owner, ability));
        }
    }
}
