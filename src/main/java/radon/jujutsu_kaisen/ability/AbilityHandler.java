package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;


public class AbilityHandler {
    public static void untrigger(LivingEntity owner,Ability ability) {
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

        Ability.Status status = ability.isTriggerable(owner);

        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            if (status == Ability.Status.SUCCESS) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                ability.run(owner);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            if (status == Ability.Status.SUCCESS || (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                cap.toggle(ability);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            if (status == Ability.Status.SUCCESS || (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                cap.channel(ability);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        }
        return status;
    }
}
