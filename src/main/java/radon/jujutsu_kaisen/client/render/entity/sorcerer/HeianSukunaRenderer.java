package radon.jujutsu_kaisen.client.render.entity.sorcerer;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.HeianSukunaEyesLayer;
import radon.jujutsu_kaisen.client.layer.HeianSukunaMouthLayer;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

public class HeianSukunaRenderer extends GeoEntityRenderer<HeianSukunaEntity> {
    private static final String LEFT_HAND = "left_hand_1";
    private static final String RIGHT_HAND = "right_hand_1";

    protected ItemStack mainHandItem;
    protected ItemStack offhandItem;

    public HeianSukunaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "heian_sukuna")));

        this.addRenderLayer(new HeianSukunaEyesLayer(this));
        this.addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, HeianSukunaEntity animatable) {
                return switch (bone.getName()) {
                    case LEFT_HAND -> animatable.isLeftHanded() ?
                            HeianSukunaRenderer.this.mainHandItem : HeianSukunaRenderer.this.offhandItem;
                    case RIGHT_HAND -> animatable.isLeftHanded() ?
                            HeianSukunaRenderer.this.offhandItem : HeianSukunaRenderer.this.mainHandItem;
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, HeianSukunaEntity animatable) {
                return switch (bone.getName()) {
                    case LEFT_HAND, RIGHT_HAND -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    default -> ItemDisplayContext.NONE;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, HeianSukunaEntity animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (stack == HeianSukunaRenderer.this.mainHandItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                    if (stack.getItem() instanceof ShieldItem) poseStack.translate(0.0F, 0.125F, -0.25F);
                } else if (stack == HeianSukunaRenderer.this.offhandItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(0.0F, 0.125F, 0.25F);
                        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    }
                }
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
        this.addRenderLayer(new HeianSukunaMouthLayer(this));
    }

    @Override
    public void preRender(PoseStack poseStack, HeianSukunaEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        this.mainHandItem = animatable.getMainHandItem();
        this.offhandItem = animatable.getOffhandItem();
    }
}
