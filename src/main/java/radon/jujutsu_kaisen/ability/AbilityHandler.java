package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;



public class AbilityHandler {
    public static Ability.Status trigger(LivingEntity owner, Ability ability) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Ability.Status.FAILURE;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            Ability.Status status;

            if ((status = ability.checkTriggerable(owner)) == Ability.Status.SUCCESS) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent(owner, ability));
                cap.addUsed(ability.getRealCost(owner));
                ability.run(owner);
            }
            return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            Ability.Status status;

            if ((status = ability.checkToggleable(owner)) == Ability.Status.SUCCESS || cap.hasToggled(ability)) {
                if (!cap.hasToggled(ability)) {
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent(owner, ability));
                }
                cap.toggle(owner, ability);
            }
            return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            Ability.Status status;

            if ((status = ability.checkChannelable(owner)) == Ability.Status.SUCCESS) {
                cap.channel(owner, ability);
            }
            return status;
        }
        return Ability.Status.SUCCESS;
    }
}
