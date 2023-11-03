package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.overlay.*;
import radon.jujutsu_kaisen.client.gui.screen.AbilityScreen;
import radon.jujutsu_kaisen.client.gui.screen.DomainScreen;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.client.model.entity.*;
import radon.jujutsu_kaisen.client.model.item.armor.InstantSpiritBodyOfDistortedKillingModel;
import radon.jujutsu_kaisen.client.particle.*;
import radon.jujutsu_kaisen.client.render.EmptyRenderer;
import radon.jujutsu_kaisen.client.render.block.DisplayCaseRenderer;
import radon.jujutsu_kaisen.client.render.block.UnlimitedVoidRenderer;
import radon.jujutsu_kaisen.client.render.entity.ChimeraShadowGardenRenderer;
import radon.jujutsu_kaisen.client.render.entity.CloneRenderer;
import radon.jujutsu_kaisen.client.render.entity.MalevolentShrineRenderer;
import radon.jujutsu_kaisen.client.render.entity.TimeCellMoonPalaceRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.*;
import radon.jujutsu_kaisen.client.render.entity.effect.*;
import radon.jujutsu_kaisen.client.render.entity.projectile.*;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.*;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.*;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.ChangeOutputC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.CommandableTargetC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.KuchisakeOnnaAnswerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.OpenInventoryCurseC2SPacket;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.io.IOException;

public class JJKClientEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onMovementInput(MovementInputUpdateEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (mc.player.hasEffect(JJKEffects.STUN.get()) || mc.player.hasEffect(JJKEffects.UNLIMITED_VOID.get())) {
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
                MobEffectInstance instance = mc.player.getEffect(JJKEffects.STUN.get());

                if ((instance != null && instance.getAmplifier() > 0) || mc.player.hasEffect(JJKEffects.UNLIMITED_VOID.get())) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                } else if (mc.options.keyShift.isDown() && event.isUseItem()) {
                    if (HelperMethods.getLookAtHit(mc.player, 64.0D) instanceof EntityHitResult hit) {
                        if (hit.getEntity() instanceof LivingEntity target) {
                            PacketHandler.sendToServer(new CommandableTargetC2SPacket(target.getUUID()));
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (event.getAction() == InputConstants.PRESS) {
                if (JJKKeys.OPEN_INVENTORY_CURSE.isDown() && mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof InventoryCurseItem) {
                    PacketHandler.sendToServer(new OpenInventoryCurseC2SPacket());
                }
                if (JJKKeys.OPEN_JUJUTSU_MENU.isDown()) {
                    mc.setScreen(new JujutsuScreen());
                }
                if (JJKKeys.ABILITY_UP.consumeClick()) {
                    AbilityOverlay.scroll(1);
                } else if (JJKKeys.ABILITY_DOWN.consumeClick()) {
                    AbilityOverlay.scroll(-1);
                }
                if (JJKKeys.SHOW_ABILITY_MENU.isDown()) {
                    mc.setScreen(new AbilityScreen());
                }
                if (JJKKeys.SHOW_DOMAIN_MENU.isDown()) {
                    mc.setScreen(new DomainScreen());
                }
                if (JJKKeys.INCREASE_OUTPUT.isDown()) {
                    ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                    PacketHandler.sendToServer(new ChangeOutputC2SPacket(ChangeOutputC2SPacket.INCREASE));
                    cap.increaseOutput(mc.player);
                }
                if (JJKKeys.DECREASE_OUTPUT.isDown()) {
                    ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                    PacketHandler.sendToServer(new ChangeOutputC2SPacket(ChangeOutputC2SPacket.DECREASE));
                    cap.decreaseOutput();
                }
            }
        }

        @SubscribeEvent
        public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
            Minecraft mc = Minecraft.getInstance();

            assert mc.player != null;

            LivingEntity target = event.getEntity();

            if (target.hasEffect(JJKEffects.UNDETECTABLE.get())) {
                Entity viewer = Minecraft.getInstance().getCameraEntity();

                if (viewer != null && target != viewer) {
                    Vec3 look = viewer.getLookAngle();
                    Vec3 start = viewer.getEyePosition();
                    Vec3 result = target.getEyePosition().subtract(start);

                    double angle = Math.acos(look.normalize().dot(result.normalize()));

                    double threshold = 0.5D;

                    if (target.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItemTags.CURSED_TOOL) ||
                            target.getItemInHand(InteractionHand.OFF_HAND).is(JJKItemTags.CURSED_TOOL)) {
                        threshold = 1.0D;
                    }

                    if (angle > threshold) {
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onClientChat(ClientChatEvent event) {
            Minecraft mc = Minecraft.getInstance();

            assert mc.level != null && mc.player != null;

            for (KuchisakeOnnaEntity curse : mc.level.getEntitiesOfClass(KuchisakeOnnaEntity.class, AABB.ofSize(mc.player.position(),
                    64.0D, 64.0D, 64.0D))) {
                curse.getCurrent().ifPresent(identifier -> {
                    event.setCanceled(true);
                    mc.player.sendSystemMessage(Component.literal(String.format("<%s> %s", mc.player.getName().getString(), event.getMessage())));
                    PacketHandler.sendToServer(new KuchisakeOnnaAnswerC2SPacket(curse.getUUID()));
                });
            }
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register((pState, pLevel, pPos, pTintIndex) -> pState.getValue(VeilBlock.COLOR).getMapColor().col,
                    JJKBlocks.VEIL.get());
        }

        @SubscribeEvent
        public static void onRegisterPlayerLayers(EntityRenderersEvent.AddLayers event) {
            if (event.getSkin("default") instanceof PlayerRenderer renderer) {
                renderer.addLayer(new JJKOverlayLayer<>(renderer));
            }
            if (event.getSkin("slim") instanceof PlayerRenderer renderer) {
                renderer.addLayer(new JJKOverlayLayer<>(renderer));
            }
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(JJKKeys.ABILITY_SCROLL);
            event.register(JJKKeys.ACTIVATE_ABILITY);
            event.register(JJKKeys.ABILITY_UP);
            event.register(JJKKeys.ABILITY_DOWN);
            event.register(JJKKeys.ACTIVATE_RCT_OR_HEAL);
            event.register(JJKKeys.OPEN_INVENTORY_CURSE);
            event.register(JJKKeys.ACTIVATE_WATER_WALKING);
            event.register(JJKKeys.SHOW_ABILITY_MENU);
            event.register(JJKKeys.SHOW_DOMAIN_MENU);
            event.register(JJKKeys.DASH);
            event.register(JJKKeys.OPEN_JUJUTSU_MENU);
            event.register(JJKKeys.INCREASE_OUTPUT);
            event.register(JJKKeys.DECREASE_OUTPUT);
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("ability_overlay", AbilityOverlay.OVERLAY);
            event.registerAboveAll("cursed_energy_overlay", CursedEnergyOverlay.OVERLAY);
            event.registerAboveAll("experience_overlay", ExperienceOverlay.OVERLAY);
            event.registerAboveAll("six_eyes_overlay", SixEyesOverlay.OVERLAY);
            event.registerAboveAll("screen_flash_overlay", ScreenFlashOverlay.OVERLAY);
        }

        @SubscribeEvent
        public static void onRegisterLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(TojiFushiguroModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(TojiFushiguroModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(TojiFushiguroModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(SukunaRyomenModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(SukunaRyomenModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(SukunaRyomenModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(SatoruGojoModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(SatoruGojoModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(SatoruGojoModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(YutaOkkotsuModel.LAYER, SkinModel::createBodyLayer);

            event.registerLayerDefinition(MegumiFushiguroModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(MegumiFushiguroModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(MegumiFushiguroModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(MegunaRyomenModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(MegunaRyomenModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(MegunaRyomenModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(YujiItadoriModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(YujiItadoriModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(YujiItadoriModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(TogeInumakiModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(TogeInumakiModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(TogeInumakiModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(SuguruGetoModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(SuguruGetoModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(SuguruGetoModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(InstantSpiritBodyOfDistortedKillingModel.LAYER_LOCATION, InstantSpiritBodyOfDistortedKillingModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new JJKResourceManager());
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(JJKEntities.RED.get(), RedRenderer::new);
            event.registerEntityRenderer(JJKEntities.BLUE.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.HOLLOW_PURPLE.get(), HollowPurpleRenderer::new);
            event.registerEntityRenderer(JJKEntities.RUGBY_FIELD_CURSE.get(), RugbyFieldCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOJI_FUSHIGURO.get(), TojiFushiguroRenderer::new);
            event.registerEntityRenderer(JJKEntities.SUKUNA.get(), SukunaRyomenRenderer::new);
            event.registerEntityRenderer(JJKEntities.DISMANTLE.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.MALEVOLENT_SHRINE.get(), MalevolentShrineRenderer::new);
            event.registerEntityRenderer(JJKEntities.SATORU_GOJO.get(), SatoruGojoRenderer::new);
            event.registerEntityRenderer(JJKEntities.FIRE_ARROW.get(), FireArrowRenderer::new);
            event.registerEntityRenderer(JJKEntities.YUTA_OKKOTSU.get(), YutaOkkotsuRenderer::new);
            event.registerEntityRenderer(JJKEntities.RIKA.get(), RikaRenderer::new);
            event.registerEntityRenderer(JJKEntities.PURE_LOVE.get(), PureLoveRenderer::new);
            event.registerEntityRenderer(JJKEntities.FIREBALL.get(), FireballRenderer::new);
            event.registerEntityRenderer(JJKEntities.JOGO.get(), JogoRenderer::new);
            event.registerEntityRenderer(JJKEntities.DAGON.get(), DagonRenderer::new);
            event.registerEntityRenderer(JJKEntities.EMBER_INSECT.get(), EmberInsectRenderer::new);
            event.registerEntityRenderer(JJKEntities.VOLCANO.get(), VolcanoRenderer::new);
            event.registerEntityRenderer(JJKEntities.METEOR.get(), MeteorRenderer::new);
            event.registerEntityRenderer(JJKEntities.THROWN_CHAIN.get(), ThrownChainItemRenderer::new);
            event.registerEntityRenderer(JJKEntities.MAHORAGA.get(), MahoragaRenderer::new);
            event.registerEntityRenderer(JJKEntities.WHEEL.get(), WheelRenderer::new);
            event.registerEntityRenderer(JJKEntities.DIVINE_DOG_WHITE.get(), DivineDogRenderer::new);
            event.registerEntityRenderer(JJKEntities.DIVINE_DOG_BLACK.get(), DivineDogRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOAD.get(), ToadRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOAD_TONGUE.get(), ToadTongueRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOAD_TOTALITY.get(), ToadRenderer::new);
            event.registerEntityRenderer(JJKEntities.RABBIT_ESCAPE.get(), RabbitRenderer::new);
            event.registerEntityRenderer(JJKEntities.MEGUMI_FUSHIGURO.get(), MegumiFushiguroRenderer::new);
            event.registerEntityRenderer(JJKEntities.NUE.get(), NueRenderer::new);
            event.registerEntityRenderer(JJKEntities.NUE_TOTALITY.get(), NueTotalityRenderer::new);
            event.registerEntityRenderer(JJKEntities.GREAT_SERPENT.get(), GreatSerpentHeadRenderer::new);
            event.registerEntityRenderer(JJKEntities.CHIMERA_SHADOW_GARDEN.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.MEGUNA.get(), MegunaRyomenRenderer::new);
            event.registerBlockEntityRenderer(JJKBlockEntities.DISPLAY_CASE.get(), DisplayCaseRenderer::new);
            event.registerEntityRenderer(JJKEntities.YUJI_ITADORI.get(), YujiItadoriRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOGE_INUMAKI.get(), TogeInumakiRenderer::new);
            event.registerEntityRenderer(JJKEntities.DIVINE_DOG_TOTALITY.get(), DivineDogTotalityRenderer::new);
            event.registerEntityRenderer(JJKEntities.FISH_CURSE.get(), FishCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.CYCLOPS_CURSE.get(), CyclopsCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.KUCHISAKE_ONNA.get(), ScissorCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.SCISSOR.get(), ScissorRenderer::new);
            event.registerEntityRenderer(JJKEntities.MAX_ELEPHANT.get(), MaxElephantRenderer::new);
            event.registerEntityRenderer(JJKEntities.PIERCING_WATER.get(), PiercingWaterRenderer::new);
            event.registerEntityRenderer(JJKEntities.JUJUTSU_LIGHTNING.get(), LightningBoltRenderer::new);
            event.registerEntityRenderer(JJKEntities.TRANQUIL_DEER.get(), TranquilDeerRenderer::new);
            event.registerEntityRenderer(JJKEntities.ZOMBA_CURSE.get(), ZombaCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.SKY_STRIKE.get(), SkyStrikeRenderer::new);
            event.registerEntityRenderer(JJKEntities.PIERCING_BULL.get(), PiercingBullRenderer::new);
            event.registerEntityRenderer(JJKEntities.AGITO.get(), AgitoRenderer::new);
            event.registerEntityRenderer(JJKEntities.WORM_CURSE.get(), WormCurseHeadRenderer::new);
            event.registerEntityRenderer(JJKEntities.MAXIMUM_UZUMAKI.get(), MaximumUzumakiRenderer::new);
            event.registerEntityRenderer(JJKEntities.MINI_UZUMAKI.get(), MiniUzumakiRenderer::new);
            event.registerEntityRenderer(JJKEntities.HOLLOW_PURPLE_EXPLOSION.get(), HollowPurpleExplosionRenderer::new);
            event.registerEntityRenderer(JJKEntities.WATERBALL.get(), WaterballRenderer::new);
            event.registerEntityRenderer(JJKEntities.CHIMERA_SHADOW_GARDEN.get(), ChimeraShadowGardenRenderer::new);
            event.registerEntityRenderer(JJKEntities.EEL_SHIKIGAMI.get(), EelShikigamiRenderer::new);
            event.registerEntityRenderer(JJKEntities.SHARK_SHIKIGAMI.get(), SharkShikigamiRenderer::new);
            event.registerEntityRenderer(JJKEntities.PIRANHA_SHIKIGAMI.get(), PiranhaShikigamiRenderer::new);
            event.registerEntityRenderer(JJKEntities.SIMPLE_DOMAIN.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.WATER_TORRENT.get(), WaterTorrentRenderer::new);
            event.registerEntityRenderer(JJKEntities.SUGURU_GETO.get(), SuguruGetoRenderer::new);
            event.registerEntityRenderer(JJKEntities.FOREST_SPIKE.get(), ForestSpikeRenderer::new);
            event.registerEntityRenderer(JJKEntities.WOOD_SEGMENT.get(), WoodSegmentRenderer::new);
            event.registerEntityRenderer(JJKEntities.WOOD_SHIELD.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.CURSED_BUD.get(), CursedBudRenderer::new);
            event.registerEntityRenderer(JJKEntities.FOREST_WAVE.get(), ForestWaveRenderer::new);
            event.registerEntityRenderer(JJKEntities.FELINE_CURSE.get(), FelineCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.LAVA_ROCK.get(), LavaRockRenderer::new);
            event.registerEntityRenderer(JJKEntities.LIGHTNING.get(), LightningRenderer::new);
            event.registerEntityRenderer(JJKEntities.HEIAN_SUKUNA.get(), HeianSukunaRenderer::new);
            event.registerEntityRenderer(JJKEntities.HANAMI.get(), HanamiRenderer::new);
            event.registerEntityRenderer(JJKEntities.PROJECTION_FRAME.get(), ProjectionFrameRenderer::new);
            event.registerEntityRenderer(JJKEntities.FOREST_ROOTS.get(), ForestRootsRenderer::new);
            event.registerEntityRenderer(JJKEntities.FILM_GAUGE.get(), FilmGaugeRenderer::new);
            event.registerEntityRenderer(JJKEntities.TIME_CELL_MOON_PALACE.get(), TimeCellMoonPalaceRenderer::new);
            event.registerEntityRenderer(JJKEntities.RAINBOW_DRAGON.get(), RainbowDragonHeadRenderer::new);
            event.registerEntityRenderer(JJKEntities.DISASTER_PLANT.get(), DisasterPlantRenderer::new);
            event.registerEntityRenderer(JJKEntities.CLONE.get(), CloneRenderer::new);
            event.registerBlockEntityRenderer(JJKBlockEntities.UNLIMITED_VOID.get(), UnlimitedVoidRenderer::new);
            event.registerEntityRenderer(JJKEntities.WORLD_SLASH.get(), EmptyRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(JJKParticles.BLACK_FLASH.get(), BlackFlashParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.VAPOR.get(), VaporParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.TRAVEL.get(), TravelParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.CURSED_ENERGY.get(), CursedEnergyParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.MIRAGE.get(), MirageParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.PROJECTION.get(), ProjectionParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.LIGHTNING.get(), LightningParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.CURSED_SPEECH.get(), CursedSpeechParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.BLOOD.get(), BloodParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
            JJKShaders.onRegisterShaders(event);
        }
    }
}
