package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.effect.FerociousBodyRepelEntity;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FerociousBodyRepelRenderer extends GeoEntityRenderer<FerociousBodyRepelEntity> {
    public FerociousBodyRepelRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "ferocious_body_repel")));
    }

    @Override
    public void preRender(PoseStack poseStack, FerociousBodyRepelEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!(animatable.getOwner() instanceof LivingEntity owner)) return;

        poseStack.translate(0.0F, animatable.getBbHeight() / 2.0F, 0.0F);

        Vec3 ownerPos = getEyePosition(owner, animatable.getBbHeight(), partialTick);
        Vec3 projectilePos = EntityUtil.getPosition(animatable, animatable.getBbHeight() / 2.0F, partialTick);
        Vec3 relative = ownerPos.subtract(projectilePos);
        float f0 = (float) relative.length();
        relative = relative.normalize();
        float f1 = (float) Math.acos(relative.y);
        float f2 = (float) Math.atan2(relative.z, relative.x);
        poseStack.mulPose(Axis.YP.rotationDegrees((((Mth.PI / 2.0F) - f2) * (180.0F / Mth.PI)) - 180.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees((f1 * (180.0F / Mth.PI)) - 90.0F));

        GeoBone tail = this.model.getBone("tail").orElseThrow();
        tail.setScaleZ(f0);

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private static Vec3 getEyePosition(Entity entity, double yOffset, float pPartialTick) {
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(entity);
        return new Vec3(
                Mth.lerp(pPartialTick, entity.xOld, entity.getX()),
                Mth.lerp(pPartialTick, entity.yOld, entity.getY()) + entity.getEyeHeight() - yOffset,
                Mth.lerp(pPartialTick, entity.zOld, entity.getZ())
        ).add(look);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FerociousBodyRepelEntity animatable) {
        ResourceLocation key = super.getTextureLocation(animatable);
        return new ResourceLocation(key.getNamespace(), key.getPath().replace(".png",
                String.format("_%s.%s", animatable.getVariant(), "png")));
    }
}
