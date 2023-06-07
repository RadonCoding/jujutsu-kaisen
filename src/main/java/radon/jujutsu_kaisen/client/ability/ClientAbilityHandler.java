package radon.jujutsu_kaisen.client.ability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;

public class ClientAbilityHandler {
    public static void trigger(Ability ability) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer entity = mc.player;

        assert entity != null;

        //Ability.Status status;

        /*if ((status = ability.checkStatus(owner)) != Ability.Status.SUCCESS) {
            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                switch (status) {
                    case NO_CHAKRA -> owner.sendSystemMessage(Component.translatable("ability.fail.not_enough_chakra"));
                    case NO_POWER -> owner.sendSystemMessage(Component.translatable("ability.fail.not_enough_power"));
                    case COOLDOWN -> mc.gui.setOverlayMessage(Component.translatable("ability.fail.cooldown",
                            cap.getRemainingCooldown(ability) / 20), false);
                }
            });
            return;
        }*/

        if (ability.getActivationType() == Ability.ActivationType.INSTANT) {
            ability.runClient(entity);
        } else if (ability.getActivationType() == Ability.ActivationType.TOGGLED) {
            entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (ability instanceof Ability.IToggled toggled) {
                    if (cap.hasToggled(ability)) {
                        entity.sendSystemMessage(toggled.getDisableMessage());
                    } else {
                        entity.sendSystemMessage(toggled.getEnableMessage());
                    }
                }
                cap.toggleAbility(ability);
            });
        }
    }
}
