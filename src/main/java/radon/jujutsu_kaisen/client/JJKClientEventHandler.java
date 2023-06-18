package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.ability.ClientAbilityHandler;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.client.gui.overlay.CursedEnergyOverlay;
import radon.jujutsu_kaisen.client.model.TojiFushiguroModel;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.client.particle.SpinningParticle;
import radon.jujutsu_kaisen.client.render.*;
import radon.jujutsu_kaisen.client.render.entity.*;
import radon.jujutsu_kaisen.client.render.entity.projectile.BlueRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.HollowPurpleRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.RedRenderer;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.TriggerAbilityC2SPacket;

public class JJKClientEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onMovementInput(MovementInputUpdateEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (mc.player.hasEffect(JJKEffects.STUN.get())) {
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
                if (mc.player.hasEffect(JJKEffects.STUN.get())) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }

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

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(JJKKeyMapping.KEY_ACTIVATE_ABILITY);
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("ability_overlay", AbilityOverlay.ABILITY_OVERLAY);
            event.registerAboveAll("cursed_energy_overlay", CursedEnergyOverlay.CURSED_ENERY_OVERLAY);
        }

        @SubscribeEvent
        public static void onRegisterLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(TojiFushiguroModel.LAYER, TojiFushiguroModel::createBodyLayer);
            event.registerLayerDefinition(TojiFushiguroModel.INNER_LAYER, TojiFushiguroModel::createBodyLayer);
            event.registerLayerDefinition(TojiFushiguroModel.OUTER_LAYER, TojiFushiguroModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(JJKEntities.RED.get(), RedRenderer::new);
            event.registerEntityRenderer(JJKEntities.BLUE.get(), BlueRenderer::new);
            event.registerEntityRenderer(JJKEntities.HOLLOW_PURPLE.get(), HollowPurpleRenderer::new);
            event.registerEntityRenderer(JJKEntities.RUGBY_FIELD_CURSE.get(), RugbyFieldCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.DOMAIN_EXPANSION.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOJI_FUSHIGURO.get(), TojiFushiguroRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
            event.register(JJKParticles.SPINNING.get(), SpinningParticle.Provider::new);
        }
    }
}
