package radon.jujutsu_kaisen.client.ability;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.JJKKeyMapping;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.TriggerAbilityC2SPacket;

public class ClientAbilityHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (event.getAction() == InputConstants.PRESS) {
                if (JJKKeyMapping.KEY_ACTIVATE_ABILITY.isDown()) {
                    Ability ability = AbilityOverlay.getSelected();

                    if (ability != null) {
                        PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                        ClientAbilityHandler.trigger(ability);
                    }
                }

                if (event.getKey() == InputConstants.KEY_UP) {
                    AbilityOverlay.scroll(1);
                } else if (event.getKey() == InputConstants.KEY_DOWN) {
                    AbilityOverlay.scroll(-1);
                }
            }
        }
    }

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
                            Math.max(1, cap.getRemainingCooldown(ability) / 20)), false);
                    case BURNOUT -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.burnout", JujutsuKaisen.MOD_ID),
                            cap.getBurnout() / 20), false);
                    case FAILURE -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.failure", JujutsuKaisen.MOD_ID)), false);
                    case DOMAIN_AMPLIFICATION -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.domain_amplification", JujutsuKaisen.MOD_ID)), false);
                }
            });
            return;
        }

        if (ability.getActivationType() == Ability.ActivationType.INSTANT) {
            ability.run(owner);
        } else if (ability.getActivationType() == Ability.ActivationType.TOGGLED) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.toggleAbility(owner, ability);
            });
        }
    }
}
