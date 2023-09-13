package radon.jujutsu_kaisen.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.item.JJKItems;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
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
                    armor.getMaterial() == JJKItems.JJKArmorMaterials.SUGURU_OUTFIT;
        }
        return false;
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
