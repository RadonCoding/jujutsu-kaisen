package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.client.gui.MeleeMenuType;
import radon.jujutsu_kaisen.client.gui.screen.MeleeScreen;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.PolymorphicSoulIsomerRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.TransfiguredSoulLargeRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.TransfiguredSoulNormalRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.TransfiguredSoulSmallRenderer;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;
import radon.jujutsu_kaisen.mixin.client.IItemInHandRendererAccessor;
import radon.jujutsu_kaisen.mixin.client.IPlayerModelAccessor;
import radon.jujutsu_kaisen.network.packet.c2s.*;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.gui.overlay.*;
import radon.jujutsu_kaisen.client.gui.screen.AbilityScreen;
import radon.jujutsu_kaisen.client.gui.screen.DomainScreen;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.client.model.entity.*;
import radon.jujutsu_kaisen.client.particle.*;
import radon.jujutsu_kaisen.client.render.EmptyRenderer;
import radon.jujutsu_kaisen.client.render.block.DisplayCaseRenderer;
import radon.jujutsu_kaisen.client.render.block.SkyRenderer;
import radon.jujutsu_kaisen.client.render.block.UnlimitedVoidRenderer;
import radon.jujutsu_kaisen.client.render.entity.*;
import radon.jujutsu_kaisen.client.render.entity.curse.*;
import radon.jujutsu_kaisen.client.render.entity.effect.*;
import radon.jujutsu_kaisen.client.render.entity.projectile.*;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.*;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.*;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.io.IOException;

public class JJKClientEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class JJKClientEventHandlerForgeEvents {
        @SubscribeEvent
        public static <T extends Item & GeoItem> void onRenderHand(RenderHandEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null || !mc.player.getItemInHand(event.getHand()).isEmpty()) return;

            if (!mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            boolean translated = false;

            for (Ability ability : cap.getToggled()) {
                if (!(ability instanceof ITransformation transformation)) continue;

                HumanoidArm arm = event.getHand() == InteractionHand.MAIN_HAND && mc.player.getMainArm() == HumanoidArm.RIGHT ? HumanoidArm.RIGHT : HumanoidArm.LEFT;

                if ((transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM && arm == HumanoidArm.RIGHT) || (transformation.getBodyPart() == ITransformation.Part.LEFT_ARM && arm == HumanoidArm.LEFT)) {
                    PlayerRenderer renderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(mc.player);
                    PlayerModel<AbstractClientPlayer> model = renderer.getModel();

                    GeoArmorRenderer<T> armor = (GeoArmorRenderer<T>) ForgeHooksClient.getArmorModel(mc.player, transformation.getItem().getDefaultInstance(), EquipmentSlot.CHEST, model);

                    VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.armorCutoutNoCull(armor.getTextureLocation((T) transformation.getItem())));

                    float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;

                    if (!translated) {
                        model.attackTime = mc.player.getAttackAnim(event.getPartialTick());

                        model.riding = mc.player.isPassenger() && (mc.player.getVehicle() != null && mc.player.getVehicle().shouldRiderSit());
                        model.young = mc.player.isBaby();

                        if (model.attackTime == 0) {
                            model.rightArmPose = HumanoidModel.ArmPose.EMPTY;
                            model.leftArmPose = HumanoidModel.ArmPose.EMPTY;

                            model.setupAnim(mc.player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                        }
                        event.getPoseStack().translate(f * 0.125F, -0.125F, 0.0F);
                        translated = true;
                    }

                    float swing = arm == HumanoidArm.RIGHT ? mc.player.getAttackAnim(event.getPartialTick()) : 0.0F;
                    float equip = 1.0F - Mth.lerp(event.getPartialTick(), ((IItemInHandRendererAccessor) mc.gameRenderer.itemInHandRenderer).getOMainHandHeightAccessor(),
                            ((IItemInHandRendererAccessor) mc.gameRenderer.itemInHandRenderer).getMainHandHeightAccessor());

                    IClientItemExtensions extensions = IClientItemExtensions.of(transformation.getItem());

                    boolean custom = extensions.applyForgeHandTransform(event.getPoseStack(), mc.player, arm, transformation.getItem().getDefaultInstance(), event.getPartialTick(), equip, swing);

                    event.getPoseStack().pushPose();

                    if (!custom) {
                        float f1 = Mth.sqrt(swing);
                        float f2 = -0.3F * Mth.sin(f1 * (float) Math.PI);
                        float f3 = 0.4F * Mth.sin(f1 * ((float) Math.PI * 2.0F));
                        float f4 = -0.4F * Mth.sin(swing * (float) Math.PI);
                        event.getPoseStack().translate(f * (f2 + 0.64000005F), f3 - 0.6F + equip * -0.6F, f4 - 0.71999997F);

                        event.getPoseStack().mulPose(Axis.YP.rotationDegrees(f * 45.0F));
                        float f5 = Mth.sin(swing * swing * (float) Math.PI);
                        float f6 = Mth.sin(f1 * (float) Math.PI);
                        event.getPoseStack().mulPose(Axis.YP.rotationDegrees(f * f6 * 70.0F));
                        event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(f * f5 * -20.0F));
                        event.getPoseStack().translate(f * -1.0F, 3.6F, 3.5F);
                        event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(f * 120.0F));
                        event.getPoseStack().mulPose(Axis.XP.rotationDegrees(200.0F));
                        event.getPoseStack().mulPose(Axis.YP.rotationDegrees(f * -135.0F));
                        event.getPoseStack().translate(f * 5.6F, 0.0F, 0.0F);

                        if (((IPlayerModelAccessor) model).getSlimAccessor()) {
                            event.getPoseStack().translate(f * 0.0546875F, 0.0F, 0.0F);
                        }
                    }
                    armor.renderToBuffer(event.getPoseStack(), consumer, event.getPackedLight(), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    event.getPoseStack().popPose();

                    event.setCanceled(custom);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            MobEffectInstance instance = mc.player.getEffect(JJKEffects.STUN.get());

            if ((instance != null && instance.getAmplifier() > 0) || mc.player.hasEffect(JJKEffects.UNLIMITED_VOID.get())) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }

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
                } else {
                    if (RotationUtil.getLookAtHit(mc.player, 64.0D, target -> target instanceof NyoiStaffEntity) instanceof EntityHitResult hit) {
                        PacketHandler.sendToServer(new NyoiStaffSummonLightningC2SPacket(hit.getEntity().getUUID()));
                    } else {
                        ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                        for (Ability ability : cap.getToggled()) {
                            if (!(ability instanceof ITransformation transformation)) continue;
                            transformation.onRightClick(mc.player);
                            PacketHandler.sendToServer(new TransformationRightClickC2SPacket(JJKAbilities.getKey(ability)));
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onRenderLevelStage(RenderLevelStageEvent event) {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
                SkyHandler.renderSky(event.getPoseStack(), event.getPartialTick(), event.getProjectionMatrix());
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (event.getAction() == InputConstants.PRESS) {
                if (JJKKeys.OPEN_INVENTORY_CURSE.isDown() && (mc.player.getItemBySlot(EquipmentSlot.CHEST).is(JJKItems.INVENTORY_CURSE.get()) ||
                        CuriosUtil.findSlot(mc.player, "body").is(JJKItems.INVENTORY_CURSE.get()))) {
                    PacketHandler.sendToServer(new OpenInventoryCurseC2SPacket());
                }
                if (JJKKeys.OPEN_JUJUTSU_MENU.isDown()) {
                    mc.setScreen(new JujutsuScreen());
                }
                if (JJKKeys.SHOW_ABILITY_MENU.isDown()) {
                    mc.setScreen(new AbilityScreen());
                }
                if (JJKKeys.SHOW_DOMAIN_MENU.isDown()) {
                    mc.setScreen(new DomainScreen());
                }
                if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.TOGGLE && JJKKeys.ACTIVATE_MELEE_MENU.isDown()) {
                    mc.setScreen(new MeleeScreen());
                }
                if (JJKKeys.INCREASE_OUTPUT.isDown()) {
                    ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                    PacketHandler.sendToServer(new ChangeOutputC2SPacket(ChangeOutputC2SPacket.INCREASE));
                    cap.increaseOutput();
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

            ClientVisualHandler.ClientData data = ClientVisualHandler.get(target);

            if (data == null) return;

            if (data.traits.contains(Trait.HEAVENLY_RESTRICTION)) {
                if (!(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity viewer)) return;

                if (JJKAbilities.hasTrait(viewer, Trait.HEAVENLY_RESTRICTION)) return;

                if (target != viewer) {
                    Vec3 look = RotationUtil.getTargetAdjustedLookAngle(viewer);
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
    public static class JJKClientEventHandlerModEvents {
        @SubscribeEvent
        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register((pState, pLevel, pPos, pTintIndex) -> pState.getValue(VeilBlock.COLOR).getMapColor().col,
                    JJKBlocks.VEIL.get());
            event.register((pState, pLevel, pPos, pTintIndex) -> GrassColor.getDefaultColor(),
                    JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get());
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
            event.register(JJKKeys.ACTIVATE_MELEE_MENU);
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
            event.registerAboveAll("ability_overlay", AbilityOverlay.OVERLAY);
            event.registerAboveAll("cursed_energy_overlay", CursedEnergyOverlay.OVERLAY);
            event.registerAboveAll("six_eyes_overlay", SixEyesOverlay.OVERLAY);
            event.registerAboveAll("screen_flash_overlay", ScreenFlashOverlay.OVERLAY);
        }

        @SubscribeEvent
        public static void onRegisterLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(CursedEnergyBlastModel.LAYER, CursedEnergyBlastModel::createBodyLayer);

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
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
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
            event.registerEntityRenderer(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), EmptyRenderer::new);
            event.registerEntityRenderer(JJKEntities.DISMANTLE.get(), DismantleRenderer::new);
            event.registerEntityRenderer(JJKEntities.MALEVOLENT_SHRINE.get(), MalevolentShrineRenderer::new);
            event.registerEntityRenderer(JJKEntities.FIRE_ARROW.get(), FireArrowRenderer::new);
            event.registerEntityRenderer(JJKEntities.PURE_LOVE.get(), PureLoveRenderer::new);
            event.registerEntityRenderer(JJKEntities.FIREBALL.get(), FireballRenderer::new);
            event.registerEntityRenderer(JJKEntities.EMBER_INSECT.get(), EmberInsectRenderer::new);
            event.registerEntityRenderer(JJKEntities.VOLCANO.get(), VolcanoRenderer::new);
            event.registerEntityRenderer(JJKEntities.METEOR.get(), MeteorRenderer::new);
            event.registerEntityRenderer(JJKEntities.THROWN_CHAIN.get(), ThrownChainProjectileRenderer::new);
            event.registerEntityRenderer(JJKEntities.CHIMERA_SHADOW_GARDEN.get(), EmptyRenderer::new);
            event.registerBlockEntityRenderer(JJKBlockEntities.DISPLAY_CASE.get(), DisplayCaseRenderer::new);
            event.registerEntityRenderer(JJKEntities.SCISSOR.get(), ScissorRenderer::new);
            event.registerEntityRenderer(JJKEntities.PIERCING_WATER.get(), PiercingWaterRenderer::new);
            event.registerEntityRenderer(JJKEntities.JUJUTSU_LIGHTNING.get(), LightningBoltRenderer::new);
            event.registerEntityRenderer(JJKEntities.SKY_STRIKE.get(), SkyStrikeRenderer::new);
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
            event.registerBlockEntityRenderer(JJKBlockEntities.UNLIMITED_VOID.get(), UnlimitedVoidRenderer::new);
            event.registerEntityRenderer(JJKEntities.SELF_EMBODIMENT_OF_PERFECTION.get(), SelfEmbodimentOfPerfectionRenderer::new);
            event.registerEntityRenderer(JJKEntities.BLACk_FLASH.get(), BlackFlashRenderer::new);
            event.registerBlockEntityRenderer(JJKBlockEntities.SKY.get(), SkyRenderer::new);
            event.registerEntityRenderer(JJKEntities.NYOI_STAFF.get(), NyoiStaffRenderer::new);
            event.registerEntityRenderer(JJKEntities.EMBER_INSECT_FLIGHT.get(), EmberInsectsFlightRenderer::new);
            event.registerEntityRenderer(JJKEntities.AIR_FRAME.get(), AirFrameRenderer::new);
            event.registerEntityRenderer(JJKEntities.FIRE_BEAM.get(), FireBeamRenderer::new);
            event.registerEntityRenderer(JJKEntities.FOREST_DASH.get(), ForestDashRenderer::new);
            event.registerEntityRenderer(JJKEntities.CURSED_ENERGY_IMBUED_ITEM.get(), ItemEntityRenderer::new);
            event.registerEntityRenderer(JJKEntities.CURSED_ENERGY_BOMB.get(), CursedEnergyBombRenderer::new);
            event.registerEntityRenderer(JJKEntities.CURSED_ENERGY_BLAST.get(), CursedEnergyBlastRenderer::new);
            event.registerEntityRenderer(JJKEntities.EEL_GRAPPLE.get(), EelGrappleRenderer::new);
            event.registerEntityRenderer(JJKEntities.TRANSFIGURED_SOUL.get(), TransfiguredSoulRenderer::new);
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
