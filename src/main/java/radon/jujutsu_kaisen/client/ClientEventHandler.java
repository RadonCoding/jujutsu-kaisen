package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JujutsuAbilities;
import radon.jujutsu_kaisen.client.ability.ClientAbilityHandler;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.client.gui.overlay.CursedEnergyOverlay;
import radon.jujutsu_kaisen.client.gui.overlay.UnlimitedVoidOverlay;
import radon.jujutsu_kaisen.client.particle.SpinningParticle;
import radon.jujutsu_kaisen.client.particle.JujutsuParticles;
import radon.jujutsu_kaisen.client.render.BlueRenderer;
import radon.jujutsu_kaisen.client.render.HollowPurpleRenderer;
import radon.jujutsu_kaisen.client.render.RedRenderer;
import radon.jujutsu_kaisen.client.render.RugbyFieldCurseRenderer;
import radon.jujutsu_kaisen.effect.JujutsuEffects;
import radon.jujutsu_kaisen.entity.JujutsuEntities;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.TriggerAbilityC2SPacket;

public class ClientEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onMovementInput(MovementInputUpdateEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (mc.player.hasEffect(JujutsuEffects.STUN.get())) {
                mc.player.input.forwardImpulse = 0.0F;
                mc.player.input.leftImpulse = 0.0F;
                mc.player.input.jumping = false;
                mc.player.input.shiftKeyDown = false;
            }
        }

        @SubscribeEvent
        public static void onPlayerMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                if (mc.player.hasEffect(JujutsuEffects.STUN.get())) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }

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

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(JujutsuKeyMapping.KEY_ACTIVATE_ABILITY);
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("ability_overlay", AbilityOverlay.ABILITY_OVERLAY);
            event.registerAboveAll("cursed_energy_overlay", CursedEnergyOverlay.CURSED_ENERY_OVERLAY);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(JujutsuEntities.RED.get(), RedRenderer::new);
            event.registerEntityRenderer(JujutsuEntities.BLUE.get(), BlueRenderer::new);
            event.registerEntityRenderer(JujutsuEntities.HOLLOW_PURPLE.get(), HollowPurpleRenderer::new);
            event.registerEntityRenderer(JujutsuEntities.RUGBY_FIELD_CURSE.get(), RugbyFieldCurseRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
            event.register(JujutsuParticles.SPINNING.get(), SpinningParticle.Provider::new);
        }
    }
}
