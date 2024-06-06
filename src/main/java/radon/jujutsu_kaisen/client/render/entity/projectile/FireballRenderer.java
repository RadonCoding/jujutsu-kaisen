package radon.jujutsu_kaisen.client.render.entity.projectile;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.projectile.FireballProjectile;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireballRenderer extends GeoEntityRenderer<FireballProjectile> {
    public FireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "fireball")));
    }

    @Override
    public void preRender(PoseStack poseStack, FireballProjectile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.translate(0.0F, animatable.getBbHeight() / 2, 0.0F);

        float yaw = Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot());
        float pitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());

        poseStack.mulPose(Axis.YN.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XN.rotationDegrees(pitch));

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected int getBlockLightLevel(@NotNull FireballProjectile pEntity, @NotNull BlockPos pPos) {
        return 15;
    }
}
