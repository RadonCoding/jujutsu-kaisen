package radon.jujutsu_kaisen.client.particle;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SlashParticle extends TextureSheetParticle {
    private final int entityId;

    private float roll;

    @Nullable
    private Entity entity;

    @Nullable
    private Vec3 offset;

    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/dismantle.png");

    protected SlashParticle(ClientLevel pLevel, double pX, double pY, double pZ, int entityId) {
        super(pLevel, pX, pY, pZ);

        this.lifetime = 2;

        this.hasPhysics = false;

        this.entityId = entityId;
    }

    @Override
    public void tick() {
        super.tick();

        this.roll = (this.random.nextFloat() - 0.5F) * 360.0F;

        if (this.entity == null) {
            this.entity = this.level.getEntity(this.entityId);
        } else {
            this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * (this.entity.getBbWidth() + this.entity.getBbHeight()) * 20.0F;
            this.setSize(this.quadSize, this.quadSize);

            Vec3 center = this.entity.position().add(0.0D, this.entity.getBbHeight() / 2, 0.0D);
            this.offset = center.add((HelperMethods.RANDOM.nextDouble() - 0.5D) * this.entity.getBbWidth(),
                    (HelperMethods.RANDOM.nextDouble() - 0.5D) * this.entity.getBbHeight(),
                    (HelperMethods.RANDOM.nextDouble() - 0.5D) * this.entity.getBbWidth());

            this.setPos(this.offset.x, this.offset.y, this.offset.z);
        }

        if (this.entity == null || this.entity.isRemoved() || !this.entity.isAlive()) {
            this.remove();
        }
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        if (this.offset == null) return;

        Minecraft mc = Minecraft.getInstance();

        PoseStack poseStack = new PoseStack();

        Vec3 cam = pRenderInfo.getPosition();
        poseStack.translate(this.offset.x - cam.x, this.offset.y - cam.y, this.offset.z - cam.z);

        poseStack.mulPose(Axis.YN.rotationDegrees(pRenderInfo.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(pRenderInfo.getXRot() - 90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(this.roll));

        poseStack.scale(1.0F, 1.0F, 0.2F);

        RenderType type = RenderType.entityTranslucent(TEXTURE);

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
        Matrix4f matrix4f = poseStack.last().pose();

        consumer.vertex(matrix4f, -this.quadSize / 2.0F, 0.0F, -1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(matrix4f, -this.quadSize / 2.0F, 0.0F, 1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(matrix4f, this.quadSize / 2.0F, 0.0F, 1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(matrix4f, this.quadSize / 2.0F, 0.0F, -1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return JJKParticleRenderTypes.CUSTOM;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet ignored) {
        }

        @Override
        public SlashParticle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z,
                                            double xSpeed, double ySpeed, double zSpeed) {
            return new SlashParticle(level, x, y, z, (int) xSpeed);
        }
    }
}
