package radon.jujutsu_kaisen.client.event;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.mixin.client.IItemInHandRendererAccessor;
import radon.jujutsu_kaisen.mixin.client.IPlayerModelAccessor;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TransformationHandler {
    @SubscribeEvent
    public static <T extends Item & GeoItem> void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || !mc.player.getItemInHand(event.getHand()).isEmpty()) return;

        IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        boolean translated = false;

        for (Ability ability : data.getToggled()) {
            if (!(ability instanceof ITransformation transformation)) continue;

            HumanoidArm arm = event.getHand() == InteractionHand.MAIN_HAND && mc.player.getMainArm() == HumanoidArm.RIGHT ? HumanoidArm.RIGHT : HumanoidArm.LEFT;

            if ((transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM && arm == HumanoidArm.RIGHT) || (transformation.getBodyPart() == ITransformation.Part.LEFT_ARM && arm == HumanoidArm.LEFT)) {
                PlayerRenderer renderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(mc.player);
                PlayerModel<AbstractClientPlayer> model = renderer.getModel();

                GeoArmorRenderer<T> armor = (GeoArmorRenderer<T>) ClientHooks.getArmorModel(mc.player, transformation.getItem().getDefaultInstance(), EquipmentSlot.CHEST, model);

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
                    float f2 = -0.3F * Mth.sin(f1 * Mth.PI);
                    float f3 = 0.4F * Mth.sin(f1 * (Mth.PI * 2.0F));
                    float f4 = -0.4F * Mth.sin(swing * Mth.PI);
                    event.getPoseStack().translate(f * (f2 + 0.64000005F), f3 - 0.6F + equip * -0.6F, f4 - 0.71999997F);

                    event.getPoseStack().mulPose(Axis.YP.rotationDegrees(f * 45.0F));
                    float f5 = Mth.sin(swing * swing * Mth.PI);
                    float f6 = Mth.sin(f1 * Mth.PI);
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
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();

        ClientVisualHandler.ClientData client = ClientVisualHandler.get(entity);

        if (client == null) return;

        if (!(event.getRenderer().getModel() instanceof PlayerModel<?> player)) return;

        for (Ability ability : client.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;

            if (transformation.isReplacement()) {
                switch (transformation.getBodyPart()) {
                    case HEAD -> {
                        player.head.visible = false;
                        player.hat.visible = false;
                    }
                    case BODY -> player.setAllVisible(false);
                    case RIGHT_ARM -> {
                        player.rightArm.visible = false;
                        player.rightSleeve.visible = false;
                    }
                    case LEFT_ARM -> {
                        player.leftArm.visible = false;
                        player.rightSleeve.visible = false;
                    }
                    case LEGS -> {
                        player.rightLeg.visible = false;
                        player.rightPants.visible = false;
                        player.leftLeg.visible = false;
                        player.leftPants.visible = false;
                    }
                }
            }

            HumanoidModel.ArmPose pose = IClientItemExtensions.of(transformation.getItem()).getArmPose(event.getEntity(), InteractionHand.MAIN_HAND, transformation.getItem().getDefaultInstance());

            if (pose != null) {
                if (transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM) {
                    player.rightArmPose = pose;
                } else if (transformation.getBodyPart() == ITransformation.Part.LEFT_ARM) {
                    player.leftArmPose = pose;
                }
            }
        }
    }
}
