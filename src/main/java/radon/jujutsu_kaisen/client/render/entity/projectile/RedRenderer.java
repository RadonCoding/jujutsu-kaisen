package radon.jujutsu_kaisen.client.render.entity.projectile;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ParticleAnimator;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.projectile.RedProjectile;

public class RedRenderer extends EntityRenderer<RedProjectile> {
    public RedRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    private static void renderBall(RedProjectile entity, float partialTicks) {
        float time = entity.getTime() + partialTicks;

        double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());

        Vec3 center = new Vec3(x, y + (entity.getBbHeight() / 2), z);

        float radius = 0.25F;

        float factor = Math.min(1.0F, time / RedProjectile.DELAY);

        int count = Math.round(radius * factor * 64);

        ParticleAnimator.sphere(entity.level(), center, () -> 0.0F, () -> radius * factor,
                () -> radius * factor * 0.25F, count, 1.0F, true, true, 5, ParticleColors.LIGHT_RED);

        ParticleAnimator.ring(entity.level(), center, count / 2, radius * 1.5F * factor, (time * 0.5F) % 360.0F,
                90.0F, 0.0F, 45.0F, ParticleColors.LIGHT_RED, radius * 1.5F * factor * 0.05F);
        ParticleAnimator.ring(entity.level(), center, count / 2, radius * 1.5F * factor, 180.0F + ((time * 0.5F) % 180.0F),
                90.0F, 0.0F, -45.0F, ParticleColors.LIGHT_RED, radius * 1.5F * factor * 0.05F);

        ParticleAnimator.ring(entity.level(), center, count / 2, radius * 1.5F * factor, 180.0F + ((time * 0.5F) % 180.0F),
                0.0F, 0.0F, 45.0F, ParticleColors.LIGHT_RED, radius * 1.5F * factor * 0.1F);
        ParticleAnimator.ring(entity.level(), center, count / 2, radius * 1.5F * factor, (time * 0.5F) % 360.0F,
                0.0F, 0.0F, -45.0F, ParticleColors.LIGHT_RED, radius * 1.5F * factor * 0.05F);

        ParticleAnimator.ring(entity.level(), center, count / 2, radius * 1.5F * factor, (time * 0.5F) % 360.0F,
                90.0F, 90.0F, 45.0F, ParticleColors.LIGHT_RED, radius * 1.5F * factor * 0.05F);
        ParticleAnimator.ring(entity.level(), center, count / 2, radius * 1.5F * factor, 180.0F + ((time * 0.5F) % 180.0F),
                90.0F, 90.0F, -45.0F, ParticleColors.LIGHT_RED, radius * 1.5F * factor * 0.05F);

        ParticleAnimator.ring(entity.level(), center, count / 2, radius * 1.5F * factor, 180.0F + ((time * 0.5F) % 180.0F),
                0.0F, 90.0F, 45.0F, ParticleColors.LIGHT_RED, radius * 1.5F * factor * 0.05F);
        ParticleAnimator.ring(entity.level(), center, count / 2, radius * 1.5F * factor, (time * 0.5F) % 360.0F,
                0.0F, 90.0F, -45.0F, ParticleColors.LIGHT_RED, radius * 1.5F * factor * 0.05F);
    }

    @Override
    public void render(RedProjectile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2, 0.0D);

        renderBall(pEntity, pPartialTick);

        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RedProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    protected int getBlockLightLevel(@NotNull RedProjectile pEntity, @NotNull BlockPos pPos) {
        return 15;
    }
}