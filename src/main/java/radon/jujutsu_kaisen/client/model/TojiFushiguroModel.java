package radon.jujutsu_kaisen.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.TojiFushiguroEntity;

public class TojiFushiguroModel extends HumanoidModel<TojiFushiguroEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro/inner_armor"), "main");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro/outer_armor"), "main");

    public TojiFushiguroModel(ModelPart pRoot) {
        super(pRoot);
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(TojiFushiguroModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64);
    }

    private static HumanoidModel.ArmPose getArmPose(TojiFushiguroEntity pEntity, InteractionHand pHand) {
        ItemStack stack = pEntity.getItemInHand(pHand);

        if (stack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (pEntity.getUsedItemHand() == pHand && pEntity.getUseItemRemainingTicks() > 0) {
                UseAnim anim = stack.getUseAnimation();

                if (anim == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (anim == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (anim == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (anim == UseAnim.CROSSBOW && pHand == pEntity.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (anim == UseAnim.SPYGLASS) {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }

                if (anim == UseAnim.TOOT_HORN) {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }
            } else if (!pEntity.swinging && stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
            HumanoidModel.ArmPose forgePose = IClientItemExtensions.of(stack).getArmPose(pEntity, pHand, stack);
            if (forgePose != null) return forgePose;
            return HumanoidModel.ArmPose.ITEM;
        }
    }

    @Override
    public void prepareMobModel(@NotNull TojiFushiguroEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        this.rightArmPose = getArmPose(pEntity, InteractionHand.MAIN_HAND);
        this.leftArmPose = getArmPose(pEntity, InteractionHand.OFF_HAND);

        super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
    }
}
