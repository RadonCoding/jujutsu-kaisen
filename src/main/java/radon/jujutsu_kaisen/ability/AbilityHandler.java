package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;


public class AbilityHandler {
    public static void stop(LivingEntity owner, Ability ability) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            if (cap.hasToggled(ability)) {
                cap.toggle(ability);
            }
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            if (cap.isChanneling(ability)) {
            cap.channel(ability);
            }
        }
    }

    public static Ability.Status trigger(LivingEntity owner, Ability ability) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            Ability.Status status;

            if ((status = ability.isTriggerable(owner)) == Ability.Status.SUCCESS) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                ability.run(owner);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            Ability.Status status;

            if ((status = ability.isTriggerable(owner)) == Ability.Status.SUCCESS || cap.hasToggled(ability)) {
                if (cap.hasToggled(ability)) {
                    cap.toggle(ability);
                } else {
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                    cap.toggle(ability);
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
                }
            }
            return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            Ability.Status status;

            if ((status = ability.isTriggerable(owner)) == Ability.Status.SUCCESS || cap.isChanneling(ability)) {
                if (cap.isChanneling(ability)) {
                    cap.channel(ability);
                } else {
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                    cap.channel(ability);
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
                }
            }
            return status;
        }
        return Ability.Status.SUCCESS;
    }
}
