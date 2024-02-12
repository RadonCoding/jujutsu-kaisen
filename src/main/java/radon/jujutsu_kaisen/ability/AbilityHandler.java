package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;


public class AbilityHandler {
    public static void untrigger(LivingEntity owner,Ability ability) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            if (data.hasToggled(ability)) {
                data.toggle(ability);
            }
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            if (data.isChanneling(ability)) {
                data.channel(ability);
            }
        }
    }

    public static Ability.Status trigger(LivingEntity owner, Ability ability) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Ability.Status.FAILURE;

        ISorcererData data = cap.getSorcererData();

        Ability.Status status = ability.isTriggerable(owner);

        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            if (status == Ability.Status.SUCCESS) {
                NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                ability.run(owner);
                NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            if (status == Ability.Status.SUCCESS || (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
                NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                data.toggle(ability);
                NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            if (status == Ability.Status.SUCCESS || (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
                NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                data.channel(ability);
                NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        }
        return status;
    }
}
