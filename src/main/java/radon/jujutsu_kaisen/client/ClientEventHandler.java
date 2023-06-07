package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JujutsuAbilities;
import radon.jujutsu_kaisen.client.ability.ClientAbilityHandler;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.client.render.RedRenderer;
import radon.jujutsu_kaisen.entity.JujutsuEntities;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.TriggerAbilityC2SPacket;

public class ClientEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (JujutsuKeyMapping.KEY_ACTIVATE_ABILITY.consumeClick()) {
                Ability ability = AbilityOverlay.getSelected();

                if (ability != null) {
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JujutsuAbilities.getKey(ability)));
                    ClientAbilityHandler.trigger(ability);
                }
            }

            if (event.getAction() == InputConstants.PRESS) {
                if (event.getKey() == InputConstants.KEY_UP) {
                    AbilityOverlay.scroll(1);
                } else if (event.getKey() == InputConstants.KEY_DOWN) {
                    AbilityOverlay.scroll(-1);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(JujutsuKeyMapping.KEY_ACTIVATE_ABILITY);
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("ability_overlay", AbilityOverlay.ABILITY_OVERLAY);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(JujutsuEntities.RED.get(), RedRenderer::new);
        }
    }
}
