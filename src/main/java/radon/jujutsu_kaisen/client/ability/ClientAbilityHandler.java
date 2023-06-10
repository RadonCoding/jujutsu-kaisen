package radon.jujutsu_kaisen.client.ability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;

public class ClientAbilityHandler {
    public static void trigger(Ability ability) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer owner = mc.player;

        assert owner != null;

        Ability.Status status;

        if ((status = ability.checkStatus(owner)) != Ability.Status.SUCCESS) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                switch (status) {
                    case ENERGY -> owner.sendSystemMessage(Component.translatable(String.format("ability.%s.fail.energy", JujutsuKaisen.MOD_ID)));
                    case COOLDOWN -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.cooldown", JujutsuKaisen.MOD_ID),
                            cap.getRemainingCooldown(ability) / 20), false);
                }
            });
            return;
        }

        if (ability.getActivationType() == Ability.ActivationType.INSTANT) {
            ability.run(owner);
        } else if (ability.getActivationType() == Ability.ActivationType.TOGGLED) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (ability instanceof Ability.IToggled toggled) {
                    if (cap.hasToggledAbility(ability)) {
                        owner.sendSystemMessage(toggled.getDisableMessage());
                    } else {
                        owner.sendSystemMessage(toggled.getEnableMessage());
                    }
                }
                cap.toggleAbility(owner, ability);
            });
        }
    }
}
