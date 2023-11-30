package radon.jujutsu_kaisen.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
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
import radon.jujutsu_kaisen.item.JJKItems;

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
            return armor.getMaterial() == JJKItems.JJKArmorMaterials.SATORU_OUTFIT ||
                    armor.getMaterial() == JJKItems.JJKArmorMaterials.SATORU_BLINDFOLD ||
                    armor.getMaterial() == JJKItems.JJKArmorMaterials.YUJI_OUTFIT ||
                    armor.getMaterial() == JJKItems.JJKArmorMaterials.MEGUMI_OUTFIT ||
                    armor.getMaterial() == JJKItems.JJKArmorMaterials.TOGE_OUTFIT ||
                    armor.getMaterial() == JJKItems.JJKArmorMaterials.YUTA_OUTFIT ||
                    armor.getMaterial() == JJKItems.JJKArmorMaterials.SUGURU_OUTFIT ||
                    armor.getMaterial() == JJKItems.JJKArmorMaterials.NAOYA_OUTFIT;
        }
        return false;
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
        ClientVisualHandler.VisualData data = ClientVisualHandler.get(pLivingEntity);

        if (data != null) {
            for (Ability ability : data.toggled()) {
                if (!(ability instanceof ITransformation transformation)) continue;

                for (int i = 0; i < 4; i++) {
                    EquipmentSlot slot = EquipmentSlot.values()[EquipmentSlot.values().length - i - 1];

                    A model = this.getArmorModel(slot);
                    ((RenderLayer<T, M>) (Object) this).getParentModel().copyPropertiesTo(model);

                    this.setPartVisibility(model, slot);

                    Model armor = this.getArmorModelHook(pLivingEntity, transformation.getItem().getDefaultInstance(), slot, model);
                    this.renderModel(pPoseStack, pBuffer, pPackedLight, (ArmorItem) transformation.getItem().asItem(), armor, this.usesInnerModel(slot),
                            1.0F, 1.0F, 1.0F, this.getArmorResource(pLivingEntity, transformation.getItem().getDefaultInstance(), slot, null));
                }
            }
        }
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"))
    public void renderArmorPieceHead(PoseStack pPoseStack, MultiBufferSource pBuffer, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo ci) {
        if (jujutsu_kaisen$isScaled(pLivingEntity, pSlot)) {
            float scale = switch (pSlot) {
                case CHEST, FEET -> 0.93F;
                case HEAD -> 0.8F;
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
}
