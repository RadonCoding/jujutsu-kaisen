package radon.jujutsu_kaisen.client.particle;

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

    @Nullable
    private Entity entity;

    private float roll;
    private Vec3 offset;

    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/dismantle.png");

    protected SlashParticle(ClientLevel pLevel, double pX, double pY, double pZ, int entityId) {
        super(pLevel, pX, pY, pZ);

        this.lifetime = 2;

        this.hasPhysics = false;

        this.entityId = entityId;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        this.roll = (this.random.nextFloat() - 0.5F) * 360.0F;

        if (this.entity == null) {
            this.entity = this.level.getEntity(this.entityId);

            if (this.entity != null) {
                Vec3 center = this.entity.position().add(0.0D, this.entity.getBbHeight() / 2.0F, 0.0D);
                this.offset = center.add((HelperMethods.RANDOM.nextDouble() - 0.5D) * this.entity.getBbWidth(),
                        (HelperMethods.RANDOM.nextDouble() - 0.5D) * this.entity.getBbHeight(),
                        (HelperMethods.RANDOM.nextDouble() - 0.5D) * this.entity.getBbWidth());
            }
        } else {
            this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * (this.entity.getBbWidth() + this.entity.getBbHeight()) * 10.0F;
        }
        if (this.entity == null || this.entity.isRemoved() || !this.entity.isAlive()) {
            this.remove();
        }
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        if (this.entity == null) return;

        Minecraft mc = Minecraft.getInstance();

        PoseStack stack = new PoseStack();

        Vec3 cam = pRenderInfo.getPosition();

        stack.pushPose();
        stack.translate(this.offset.x - cam.x, this.offset.y - cam.y, this.offset.z - cam.z);
        stack.mulPose(Axis.YN.rotationDegrees(pRenderInfo.getYRot()));
        stack.mulPose(Axis.XP.rotationDegrees(pRenderInfo.getXRot() - 90.0F));
        stack.mulPose(Axis.YP.rotationDegrees(this.roll));

        stack.scale(1.0F, 1.0F, 0.2F);

        RenderType type = RenderType.entityTranslucent(TEXTURE);

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
        Matrix4f pose = stack.last().pose();

        consumer.vertex(pose, -this.quadSize, 0.0F, -1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, -this.quadSize, 0.0F, 1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, this.quadSize, 0.0F, 1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, this.quadSize, 0.0F, -1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();

        stack.popPose();
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
