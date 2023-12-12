package radon.jujutsu_kaisen.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.item.armor.JJKDeflatedArmorMaterial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
    @Shadow
    protected abstract void setPartVisibility(A pModel, EquipmentSlot pSlot);

    @Shadow
    protected abstract A getArmorModel(EquipmentSlot pSlot);

    @Shadow
    protected abstract Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model);

    @Shadow
    protected abstract boolean usesInnerModel(EquipmentSlot pSlot);

    @Shadow
    public abstract ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type);

    @Shadow
    protected abstract void renderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ArmorItem pArmorItem, Model pModel, boolean pWithGlint, float pRed, float pGreen, float pBlue, ResourceLocation armorResource);

    @Unique
    private static boolean jujutsu_kaisen$isScaled(LivingEntity entity, EquipmentSlot slot) {
        ItemStack stack = entity.getItemBySlot(slot);

        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getMaterial() instanceof JJKDeflatedArmorMaterial;
        }
        return false;
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
        ClientVisualHandler.VisualData data = ClientVisualHandler.get(pLivingEntity);

        if (data != null) {
            Set<EquipmentSlot> hidden = new HashSet<>();

            for (Ability ability : data.toggled) {
                if (!(ability instanceof ITransformation transformation)) continue;

                if (transformation.isReplacement()) {
                    hidden.add(transformation.getBodyPart().getSlot());
                }
            }

            for (Ability ability : data.toggled) {
                if (!(ability instanceof ITransformation transformation)) continue;

                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (transformation.getBodyPart().getSlot() != slot && hidden.contains(slot)) continue;

                    A model = this.getArmorModel(slot);
                    ((RenderLayer<T, M>) (Object) this).getParentModel().copyPropertiesTo(model);

                    this.setPartVisibility(model, slot);

                    pPoseStack.pushPose();

                    if (transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM ||
                            transformation.getBodyPart() == ITransformation.Part.LEFT_ARM) {
                        if (((RenderLayer<T, M>) (Object) this).getParentModel() instanceof PlayerModel<?> player) {
                            if (((IPlayerModelAccessor) player).getSlim()) {
                                pPoseStack.translate(transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM ? 0.0625 : -0.0625, 0.0F, 0.0F);
                            }
                        }
                    }
                    Model armor = this.getArmorModelHook(pLivingEntity, transformation.getItem().getDefaultInstance(), slot, model);
                    this.renderModel(pPoseStack, pBuffer, pPackedLight, (ArmorItem) transformation.getItem().asItem(), armor, this.usesInnerModel(slot),
                            1.0F, 1.0F, 1.0F, this.getArmorResource(pLivingEntity, transformation.getItem().getDefaultInstance(), slot, null));
                    pPoseStack.popPose();
                }
            }
        }
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"))
    public void renderArmorPieceHead(PoseStack pPoseStack, MultiBufferSource pBuffer, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo ci) {
        if (jujutsu_kaisen$isScaled(pLivingEntity, pSlot)) {
            JJKDeflatedArmorMaterial material = (JJKDeflatedArmorMaterial) ((ArmorItem) pLivingEntity.getItemBySlot(pSlot).getItem()).getMaterial();

            float scale = switch (pSlot) {
                case HEAD -> material.headDeflate();
                case CHEST -> material.chestDeflate();
                case LEGS -> material.legsDeflate();
                case FEET -> material.feetDeflate();
                default -> 1.0F;
            };

            pPoseStack.pushPose();

            if (pSlot == EquipmentSlot.FEET) {
                pPoseStack.translate(0.0F, 1.0F - scale, 0.0F);
            }
            pPoseStack.scale(scale, scale, scale);
        }
    }

    @Inject(method = "renderArmorPiece", at = @At("TAIL"))
    public void renderArmorPieceTail(PoseStack pPoseStack, MultiBufferSource pBuffer, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo ci) {
        if (jujutsu_kaisen$isScaled(pLivingEntity, pSlot)) {
            pPoseStack.popPose();
        }
    }

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;setPartVisibility(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/EquipmentSlot;)V", shift = At.Shift.AFTER))
    public void renderArmorPiece(PoseStack pPoseStack, MultiBufferSource pBuffer, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo ci) {
        ClientVisualHandler.VisualData data = ClientVisualHandler.get(pLivingEntity);

        if (data == null) return;

        for (Ability ability : data.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;

            if (transformation.isReplacement()) {
                switch (transformation.getBodyPart()) {
                    case HEAD -> {
                        pModel.head.visible = false;
                        pModel.hat.visible = false;
                    }
                    case BODY -> pModel.setAllVisible(false);
                    case RIGHT_ARM -> pModel.rightArm.visible = false;
                    case LEFT_ARM -> pModel.leftArm.visible = false;
                    case LEGS -> {
                        pModel.rightLeg.visible = false;
                        pModel.leftLeg.visible = false;
                    }
                }
            }
        }
    }
}
