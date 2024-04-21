package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.gui.screen.AltarScreen;
import radon.jujutsu_kaisen.client.gui.screen.BountyScreen;
import radon.jujutsu_kaisen.client.gui.screen.VeilRodScreen;
import radon.jujutsu_kaisen.client.model.entity.effect.CursedEnergyBlastModel;
import radon.jujutsu_kaisen.client.model.entity.effect.FireBeamModel;
import radon.jujutsu_kaisen.client.render.block.*;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.PolymorphicSoulIsomerRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.TransfiguredSoulLargeRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.TransfiguredSoulNormalRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.TransfiguredSoulSmallRenderer;
import radon.jujutsu_kaisen.entity.base.IControllableFlyingRide;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;
import radon.jujutsu_kaisen.menu.JJKMenus;
import radon.jujutsu_kaisen.network.packet.c2s.*;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.client.gui.overlay.*;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.client.model.entity.*;
import radon.jujutsu_kaisen.client.particle.*;
import radon.jujutsu_kaisen.client.render.EmptyRenderer;
import radon.jujutsu_kaisen.client.render.entity.*;
import radon.jujutsu_kaisen.client.render.entity.curse.*;
import radon.jujutsu_kaisen.client.render.entity.effect.*;
import radon.jujutsu_kaisen.client.render.entity.projectile.*;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.*;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.*;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.awt.event.KeyEvent;
import java.io.IOException;

public class JJKClientEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onPlayerMouseClick(InputEvent.MouseButton.Pre event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (event.getAction() == InputConstants.PRESS && event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT) {
                if (mc.options.keyShift.isDown()) {
                    if (RotationUtil.getLookAtHit(mc.player, 64.0D) instanceof EntityHitResult hit) {
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

            if (event.getKey() == KeyEvent.VK_SPACE) {
                if (event.getAction() == InputConstants.PRESS || event.getAction() == InputConstants.RELEASE) {
                    boolean down = event.getAction() == InputConstants.PRESS;

                    if (mc.player.getVehicle() instanceof IControllableFlyingRide listener) {
                        listener.setJump(down);
                        PacketHandler.sendToServer(new JumpInputListenerC2SPacket(down));
                    } else if (mc.player.getFirstPassenger() instanceof IControllableFlyingRide listener) {
                        listener.setJump(down);
                        PacketHandler.sendToServer(new JumpInputListenerC2SPacket(down));
                    }
                }
            }

            if (event.getAction() == InputConstants.PRESS) {
                if (JJKKeys.OPEN_INVENTORY_CURSE.isDown() && (mc.player.getItemBySlot(EquipmentSlot.CHEST).is(JJKItems.INVENTORY_CURSE.get()) ||
                        CuriosUtil.findSlot(mc.player, "body").is(JJKItems.INVENTORY_CURSE.get()))) {
                    PacketHandler.sendToServer(new OpenInventoryCurseC2SPacket());
                }

                if (mc.options.keyJump.consumeClick()) {
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.AIR_JUMP.getId()));
                }
            }
        }

        @SubscribeEvent
        public static void onClientChat(ClientChatEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.level == null || mc.player == null) return;

            for (ScissorEntity curse : mc.level.getEntitiesOfClass(ScissorEntity.class, AABB.ofSize(mc.player.position(),
                    16.0D, 16.0D, 16.0D))) {
                if (curse.getVictim() != mc.player) continue;

                mc.player.sendSystemMessage(Component.literal(String.format("<%s> %s", mc.player.getName().getString(), event.getMessage())));
                PacketHandler.sendToServer(new ScissorsAnswerC2SPacket(curse.getUUID()));
                event.setCanceled(true);

                break;
            }
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register((pState, pLevel, pPos, pTintIndex) -> pState.getValue(VeilBlock.COLOR).getMapColor().col,
                    JJKBlocks.VEIL.get());
            event.register((pState, pLevel, pPos, pTintIndex) -> GrassColor.getDefaultColor(),
                    JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get());
        }

        @SubscribeEvent
        public static void onRegisterPlayerLayers(EntityRenderersEvent.AddLayers event) {
            if (event.getSkin(PlayerSkin.Model.WIDE) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new JJKOverlayLayer<>(renderer));
            }
            if (event.getSkin(PlayerSkin.Model.SLIM) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new JJKOverlayLayer<>(renderer));
            }
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(JJKKeys.ACTIVATE_MELEE_MENU);
            event.register(JJKKeys.MELEE_MENU_UP);
            event.register(JJKKeys.MELEE_MENU_DOWN);
            event.register(JJKKeys.ACTIVATE_ABILITY);
            event.register(JJKKeys.ACTIVATE_RCT_OR_HEAL);
            event.register(JJKKeys.OPEN_INVENTORY_CURSE);
            event.register(JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD);
            event.register(JJKKeys.SHOW_ABILITY_MENU);
            event.register(JJKKeys.SHOW_DOMAIN_MENU);
            event.register(JJKKeys.DASH);
            event.register(JJKKeys.OPEN_JUJUTSU_MENU);
            event.register(JJKKeys.INCREASE_OUTPUT);
            event.register(JJKKeys.DECREASE_OUTPUT);
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll(new ResourceLocation(JujutsuKaisen.MOD_ID, "ability_overlay"), AbilityOverlay.OVERLAY);
            event.registerAboveAll(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_energy_overlay"), CursedEnergyOverlay.OVERLAY);
            event.registerAboveAll(new ResourceLocation(JujutsuKaisen.MOD_ID, "six_eyes_overlay"), SixEyesOverlay.OVERLAY);
            event.registerAboveAll(new ResourceLocation(JujutsuKaisen.MOD_ID, "screen_flash_overlay"), ScreenFlashOverlay.OVERLAY);
            event.registerAboveAll(new ResourceLocation(JujutsuKaisen.MOD_ID, "mission_overlay"), MissionOverlay.OVERLAY);
        }

        @SubscribeEvent
        public static void onRegisterLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(CursedEnergyBlastModel.LAYER, CursedEnergyBlastModel::createBodyLayer);
            event.registerLayerDefinition(FireBeamModel.LAYER, FireBeamModel::createBodyLayer);

            event.registerLayerDefinition(TojiFushiguroModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(TojiFushiguroModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(TojiFushiguroModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(SatoruGojoModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(SatoruGojoModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(SatoruGojoModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(YutaOkkotsuModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(YutaOkkotsuModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(YutaOkkotsuModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(MegumiFushiguroModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(MegumiFushiguroModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(MegumiFushiguroModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(YujiItadoriModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(YujiItadoriModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(YujiItadoriModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(TogeInumakiModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(TogeInumakiModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(TogeInumakiModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(SuguruGetoModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(SuguruGetoModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(SuguruGetoModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(NaoyaZeninModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(NaoyaZeninModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(NaoyaZeninModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(HajimeKashimoModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(HajimeKashimoModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(HajimeKashimoModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(MakiZeninModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(MakiZeninModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(MakiZeninModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(AoiTodoModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(AoiTodoModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(AoiTodoModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(MiwaKasumiModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(MiwaKasumiModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(MiwaKasumiModel.OUTER_LAYER, SkinModel::createOuterLayer);

            event.registerLayerDefinition(WindowModel.LAYER, SkinModel::createBodyLayer);
            event.registerLayerDefinition(WindowModel.INNER_LAYER, SkinModel::createInnerLayer);
            event.registerLayerDefinition(WindowModel.OUTER_LAYER, SkinModel::createOuterLayer);
        }

        @SubscribeEvent
        public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
            event.register(JJKMenus.ALTAR.get(), AltarScreen::new);
            event.register(JJKMenus.VEIL_ROD.get(), VeilRodScreen::new);
            event.register(JJKMenus.BOUNTY.get(), BountyScreen::new);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.GENUINE_MUTUAL_LOVE.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.MALEVOLENT_SHRINE.get(), MalevolentShrineRenderer::new);
            event.registerEntityRenderer(JJKEntities.CHIMERA_SHADOW_GARDEN.get(), ChimeraShadowGardenRenderer::new);

            event.registerEntityRenderer(JJKEntities.VEIL.get(), EmptyRenderer::new);

            event.registerEntityRenderer(JJKEntities.JOGO.get(), JogoRenderer::new);
            event.registerEntityRenderer(JJKEntities.JOGOAT.get(), JogoatRenderer::new);
            event.registerEntityRenderer(JJKEntities.DAGON.get(), DagonRenderer::new);
            event.registerEntityRenderer(JJKEntities.HANAMI.get(), HanamiRenderer::new);
            event.registerEntityRenderer(JJKEntities.RUGBY_FIELD_CURSE.get(), RugbyFieldCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.FISH_CURSE.get(), FishCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.CYCLOPS_CURSE.get(), CyclopsCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.KUCHISAKE_ONNA.get(), ScissorCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.ZOMBA_CURSE.get(), ZombaCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.WORM_CURSE.get(), WormCurseHeadRenderer::new);
            event.registerEntityRenderer(JJKEntities.FELINE_CURSE.get(), FelineCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.FUGLY_CURSE.get(), FuglyCurseRenderer::new);;
            event.registerEntityRenderer(JJKEntities.BIRD_CURSE.get(), BirdCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.FINGER_BEARER.get(), FingerBearerRenderer::new);
            event.registerEntityRenderer(JJKEntities.RAINBOW_DRAGON.get(), RainbowDragonHeadRenderer::new);
            event.registerEntityRenderer(JJKEntities.DINO_CURSE.get(), DinoCurseRenderer::new);
            event.registerEntityRenderer(JJKEntities.KO_GUY.get(), KoGuyRenderer::new);
            event.registerEntityRenderer(JJKEntities.ABSORBED_PLAYER.get(), AbsorbedPlayerEntity::new);

            event.registerEntityRenderer(JJKEntities.SUKUNA.get(), SukunaRenderer::new);
            event.registerEntityRenderer(JJKEntities.HEIAN_SUKUNA.get(), HeianSukunaRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOJI_FUSHIGURO.get(), TojiFushiguroRenderer::new);
            event.registerEntityRenderer(JJKEntities.SATORU_GOJO.get(), SatoruGojoRenderer::new);
            event.registerEntityRenderer(JJKEntities.SUGURU_GETO.get(), SuguruGetoRenderer::new);
            event.registerEntityRenderer(JJKEntities.YUTA_OKKOTSU.get(), YutaOkkotsuRenderer::new);
            event.registerEntityRenderer(JJKEntities.MEGUMI_FUSHIGURO.get(), MegumiFushiguroRenderer::new);
            event.registerEntityRenderer(JJKEntities.NAOYA_ZENIN.get(), NaoyaZeninRenderer::new);
            event.registerEntityRenderer(JJKEntities.YUJI_ITADORI.get(), YujiItadoriRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOGE_INUMAKI.get(), TogeInumakiRenderer::new);
            event.registerEntityRenderer(JJKEntities.MAKI_ZENIN.get(), MakiZeninRenderer::new);
            event.registerEntityRenderer(JJKEntities.HAJIME_KASHIMO.get(), HajimeKashimoRenderer::new);
            event.registerEntityRenderer(JJKEntities.AOI_TODO.get(), AoiTodoRenderer::new);
            event.registerEntityRenderer(JJKEntities.MIWA_KASUMI.get(), MiwaKasumiRenderer::new);
            event.registerEntityRenderer(JJKEntities.WINDOW.get(), WindowRenderer::new);

            event.registerEntityRenderer(JJKEntities.RIKA.get(), RikaRenderer::new);

            event.registerEntityRenderer(JJKEntities.MAHORAGA.get(), MahoragaRenderer::new);
            event.registerEntityRenderer(JJKEntities.DIVINE_DOG_WHITE.get(), DivineDogRenderer::new);
            event.registerEntityRenderer(JJKEntities.DIVINE_DOG_BLACK.get(), DivineDogRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOAD.get(), ToadRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOAD_TONGUE.get(), ToadTongueRenderer::new);
            event.registerEntityRenderer(JJKEntities.TOAD_FUSION.get(), ToadRenderer::new);
            event.registerEntityRenderer(JJKEntities.RABBIT_ESCAPE.get(), RabbitEscapeRenderer::new);
            event.registerEntityRenderer(JJKEntities.NUE.get(), NueRenderer::new);
            event.registerEntityRenderer(JJKEntities.NUE_TOTALITY.get(), NueTotalityRenderer::new);
            event.registerEntityRenderer(JJKEntities.GREAT_SERPENT.get(), GreatSerpentHeadRenderer::new);
            event.registerEntityRenderer(JJKEntities.MAX_ELEPHANT.get(), MaxElephantRenderer::new);
            event.registerEntityRenderer(JJKEntities.DIVINE_DOG_TOTALITY.get(), DivineDogTotalityRenderer::new);
            event.registerEntityRenderer(JJKEntities.PIERCING_BULL.get(), PiercingBullRenderer::new);
            event.registerEntityRenderer(JJKEntities.TRANQUIL_DEER.get(), TranquilDeerRenderer::new);
            event.registerEntityRenderer(JJKEntities.AGITO.get(), AgitoRenderer::new);

            event.registerEntityRenderer(JJKEntities.TRANSFIGURED_SOUL_SMALL.get(), TransfiguredSoulSmallRenderer::new);
            event.registerEntityRenderer(JJKEntities.TRANSFIGURED_SOUL_NORMAL.get(), TransfiguredSoulNormalRenderer::new);
            event.registerEntityRenderer(JJKEntities.TRANSFIGURED_SOUL_LARGE.get(), TransfiguredSoulLargeRenderer::new);
            event.registerEntityRenderer(JJKEntities.POLYMORPHIC_SOUL_ISOMER.get(), PolymorphicSoulIsomerRenderer::new);

            event.registerEntityRenderer(JJKEntities.TOAD_TONGUE.get(), ToadTongueRenderer::new);
            event.registerEntityRenderer(JJKEntities.WHEEL.get(), WheelRenderer::new);
            event.registerEntityRenderer(JJKEntities.RED.get(), RedRenderer::new);
            event.registerEntityRenderer(JJKEntities.BLUE.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.HOLLOW_PURPLE.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.DISMANTLE.get(), DismantleRenderer::new);
            event.registerEntityRenderer(JJKEntities.BIG_DISMANTLE.get(), DismantleRenderer::new);
            event.registerEntityRenderer(JJKEntities.WORLD_SLASH.get(), WorldSlashRenderer::new);
            event.registerEntityRenderer(JJKEntities.FIRE_ARROW.get(), FireArrowRenderer::new);
            event.registerEntityRenderer(JJKEntities.PURE_LOVE.get(), PureLoveRenderer::new);
            event.registerEntityRenderer(JJKEntities.FIREBALL.get(), FireballRenderer::new);
            event.registerEntityRenderer(JJKEntities.EMBER_INSECT.get(), EmberInsectRenderer::new);
            event.registerEntityRenderer(JJKEntities.VOLCANO.get(), VolcanoRenderer::new);
            event.registerEntityRenderer(JJKEntities.METEOR.get(), MeteorRenderer::new);
            event.registerEntityRenderer(JJKEntities.THROWN_CHAIN.get(), ThrownChainProjectileRenderer::new);
            event.registerEntityRenderer(JJKEntities.SCISSOR.get(), ScissorRenderer::new);
            event.registerEntityRenderer(JJKEntities.PIERCING_WATER.get(), PiercingWaterRenderer::new);
            event.registerEntityRenderer(JJKEntities.JUJUTSU_LIGHTNING.get(), LightningBoltRenderer::new);
            event.registerEntityRenderer(JJKEntities.SKY_STRIKE.get(), SkyStrikeRenderer::new);
            event.registerEntityRenderer(JJKEntities.MAXIMUM_UZUMAKI.get(), MaximumUzumakiRenderer::new);
            event.registerEntityRenderer(JJKEntities.MINI_UZUMAKI.get(), MiniUzumakiRenderer::new);
            event.registerEntityRenderer(JJKEntities.HOLLOW_PURPLE_EXPLOSION.get(), HollowPurpleExplosionRenderer::new);
            event.registerEntityRenderer(JJKEntities.WATERBALL.get(), WaterballRenderer::new);
            event.registerEntityRenderer(JJKEntities.EEL_SHIKIGAMI.get(), EelShikigamiRenderer::new);
            event.registerEntityRenderer(JJKEntities.SHARK_SHIKIGAMI.get(), SharkShikigamiRenderer::new);
            event.registerEntityRenderer(JJKEntities.PIRANHA_SHIKIGAMI.get(), PiranhaShikigamiRenderer::new);
            event.registerEntityRenderer(JJKEntities.SIMPLE_DOMAIN.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.WATER_TORRENT.get(), WaterTorrentRenderer::new);
            event.registerEntityRenderer(JJKEntities.FOREST_SPIKE.get(), ForestSpikeRenderer::new);
            event.registerEntityRenderer(JJKEntities.WOOD_SEGMENT.get(), WoodSegmentRenderer::new);
            event.registerEntityRenderer(JJKEntities.WOOD_SHIELD_SEGMENT.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.WOOD_SHIELD.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.CURSED_BUD.get(), CursedBudRenderer::new);
            event.registerEntityRenderer(JJKEntities.FOREST_WAVE.get(), ForestWaveRenderer::new);
            event.registerEntityRenderer(JJKEntities.LAVA_ROCK.get(), LavaRockRenderer::new);
            event.registerEntityRenderer(JJKEntities.LIGHTNING.get(), LightningRenderer::new);
            event.registerEntityRenderer(JJKEntities.PROJECTION_FRAME.get(), ProjectionFrameRenderer::new);
            event.registerEntityRenderer(JJKEntities.FOREST_ROOTS.get(), ForestRootsRenderer::new);
            event.registerEntityRenderer(JJKEntities.FILM_GAUGE.get(), FilmGaugeRenderer::new);
            event.registerEntityRenderer(JJKEntities.TIME_CELL_MOON_PALACE.get(), TimeCellMoonPalaceRenderer::new);
            event.registerEntityRenderer(JJKEntities.DISASTER_PLANT.get(), DisasterPlantRenderer::new);
            event.registerEntityRenderer(JJKEntities.SELF_EMBODIMENT_OF_PERFECTION.get(), SelfEmbodimentOfPerfectionRenderer::new);
            event.registerEntityRenderer(JJKEntities.BLACk_FLASH.get(), BlackFlashRenderer::new);
            event.registerEntityRenderer(JJKEntities.NYOI_STAFF.get(), NyoiStaffRenderer::new);
            event.registerEntityRenderer(JJKEntities.MIMICRY_KATANA.get(), MimicryKatanaRenderer::new);
            event.registerEntityRenderer(JJKEntities.EMBER_INSECT_FLIGHT.get(), EmberInsectFlightRenderer::new);
            event.registerEntityRenderer(JJKEntities.AIR_FRAME.get(), AirFrameRenderer::new);
            event.registerEntityRenderer(JJKEntities.FIRE_BEAM.get(), FireBeamRenderer::new);
            event.registerEntityRenderer(JJKEntities.FOREST_DASH.get(), ForestDashRenderer::new);
            event.registerEntityRenderer(JJKEntities.CURSED_ENERGY_IMBUED_ITEM.get(), ItemEntityRenderer::new);
            event.registerEntityRenderer(JJKEntities.CURSED_ENERGY_BOMB.get(), CursedEnergyBombRenderer::new);
            event.registerEntityRenderer(JJKEntities.CURSED_ENERGY_BLAST.get(), CursedEnergyBlastRenderer::new);
            event.registerEntityRenderer(JJKEntities.EEL_GRAPPLE.get(), EelGrappleRenderer::new);
            event.registerEntityRenderer(JJKEntities.TRANSFIGURED_SOUL.get(), TransfiguredSoulRenderer::new);
            event.registerEntityRenderer(JJKEntities.ELECTRIC_BLAST.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.BODY_REPEL.get(), BodyRepelRenderer::new);
            event.registerEntityRenderer(JJKEntities.FEROCIOUS_BODY_REPEL.get(), FerociousBodyRepelRenderer::new);

            event.registerBlockEntityRenderer(JJKBlockEntities.UNLIMITED_VOID.get(), UnlimitedVoidBlockRenderer::new);
            event.registerBlockEntityRenderer(JJKBlockEntities.SELF_EMBODIMENT_OF_PERFECTION.get(), SelfEmbodimentOfPerfectionBlockRenderer::new);
            event.registerBlockEntityRenderer(JJKBlockEntities.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get(), HorizonOfTheCaptivatingSkandhaBlockRenderer::new);
            event.registerBlockEntityRenderer(JJKBlockEntities.SHINING_SEA_OF_FLOWERS.get(), ShiningSeaOfFlowersBlockRenderer::new);
            event.registerBlockEntityRenderer(JJKBlockEntities.AUTHENTIC_MUTUAL_LOVE.get(), AuthenticMutualLoveBlockRenderer::new);
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
            event.registerSpriteSet(JJKParticles.SLASH.get(), SlashParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.EMITTING_LIGHTNING.get(), EmittingLightningParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.FIRE.get(), FireParticle.Provider::new);
            event.registerSpriteSet(JJKParticles.SMOKE.get(), BetterSmokeParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
            JJKShaders.onRegisterShaders(event);
        }
    }
}
