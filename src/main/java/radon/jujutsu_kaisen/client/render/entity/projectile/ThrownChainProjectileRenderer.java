package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.visual.visual.BlueFistsVisual;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ThrownChainProjectileRenderer extends EntityRenderer<ThrownChainProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/chain_link.png");

    private static final float SCALE = 0.75F;

    private final ItemRenderer itemRenderer;

    public ThrownChainProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.itemRenderer = pContext.getItemRenderer();
    }

    @Override
    public void render(@NotNull ThrownChainProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (!(pEntity.getOwner() instanceof LivingEntity owner)) return;

        Vec3 ownerPos = BlueFistsVisual.getArmPos(owner, HumanoidArm.RIGHT)
                // This is to position the chain on the model
                .add(RotationUtil.calculateViewVector(0.0F, owner.yBodyRot).scale(0.28125D))
                // This is to center the chain texture thingy
                .add(RotationUtil.calculateViewVector(0.0F, owner.yBodyRot - 90.0F).scale(0.0625D / 2.0F));

        if (!pEntity.isReleased()) {
            Vec3 displacement = ownerPos
                    .subtract(0.0D, pEntity.getBbHeight() / 2.0F, 0.0D)
                    .subtract(EntityUtil.getPosition(pEntity, pPartialTick));
            pPoseStack.translate(displacement.x, displacement.y, displacement.z);
        }

        Vec3 relative = pEntity.calculateOffset(pEntity.getTime() + pPartialTick);
        Vec3 offset = relative
                .xRot(90.0F * (Mth.PI / 180.0F))
                .yRot(-(float) Mth.lerp(pPartialTick, owner.yBodyRotO, owner.yBodyRot) * (Mth.PI / 180.0F));

        pPoseStack.pushPose();

        if (!pEntity.isReleased()) {
            pPoseStack.translate(-offset.x, -offset.y, -offset.z);
        }

        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F * (1.0F / SCALE), 0.0F);
        pPoseStack.scale(SCALE, SCALE, SCALE);

        float yaw;
        float pitch;

        if (pEntity.isReleased()) {
            yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
            pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());
        } else {
            yaw = 270.0F - Mth.lerp(pPartialTick, owner.yBodyRotO, owner.yBodyRot);
            pitch = 360.0F - ((float) Math.atan2(relative.z, relative.x) * (180.0F / Mth.PI));
        }

        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw + 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(135.0F - pitch));

        ItemStack stack = pEntity.getStack();

        BakedModel model = this.itemRenderer.getModel(stack, null, null, pEntity.getId());
        this.itemRenderer.render(stack, ItemDisplayContext.GUI, false, pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, model);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2.0F * (1.0F / SCALE), 0.0F);

        Vec3 projectilePos = pEntity.isReleased() ? EntityUtil.getPosition(pEntity, pEntity.getBbHeight() / 2.0F, pPartialTick) : ownerPos.add(offset);

        Vec3 direction = ownerPos.subtract(projectilePos);
        float f0 = (float) direction.length();
        direction = direction.normalize();

        Quaternionf rotation = new Quaternionf().rotateTo(new Vector3f(0, 1, 0), direction.toVector3f());
        pPoseStack.mulPose(rotation);

        float f3 = -1.0F;
        int j = 255;
        int k = 255;
        int l = 255;
        float f4 = 0.0F;
        float f5 = 0.2F;
        float f6 = 0.0F;
        float f7 = -0.2F;
        float f8 = Mth.cos(f3 + (Mth.PI / 2.0F)) * 0.2F;
        float f9 = Mth.sin(f3 + (Mth.PI / 2.0F)) * 0.2F;
        float f10 = Mth.cos(f3 + (Mth.PI * 1.5F)) * 0.2F;
        float f11 = Mth.sin(f3 + (Mth.PI * 1.5F)) * 0.2F;
        float f12 = 0.0F;
        float f13 = f0 + f12;
        float f14 = 0.75F;
        float f15 = f0 + f14;

        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        PoseStack.Pose pose = pPoseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        vertex(consumer, matrix4f, matrix3f, f4, f0, f5, j, k, l, 0.4999F, f13, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f4, 0.0F, f5, j, k, l, 0.4999F, f12, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f6, 0.0F, f7, j, k, l, 0.0F, f12, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f6, f0, f7, j, k, l, 0.0F, f13, pPackedLight);

        vertex(consumer, matrix4f, matrix3f, f8, f0, f9, j, k, l, 0.4999F, f15, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f8, 0.0F, f9, j, k, l, 0.4999F, f14, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f10, 0.0F, f11, j, k, l, 0.0F, f14, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f10, f0, f11, j, k, l, 0.0F, f15, pPackedLight);
        pPoseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, int r, int g, int b, float u, float v, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(r, g, b, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrownChainProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
