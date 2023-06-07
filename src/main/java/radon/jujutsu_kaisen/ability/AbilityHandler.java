package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;

public class AbilityHandler {
    public static void trigger(LivingEntity entity, Ability ability) {
        /*if (ability.checkStatus(entity) != Ability.Status.SUCCESS) {
            return;
        }*/

        if (ability.getActivationType() == Ability.ActivationType.INSTANT) {
            ability.runServer(entity);
        } else if (ability.getActivationType() == Ability.ActivationType.TOGGLED) {
            entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.toggleAbility(ability));
        }
    }
}
