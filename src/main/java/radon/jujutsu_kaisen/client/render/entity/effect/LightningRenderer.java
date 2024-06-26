package radon.jujutsu_kaisen.client.render.entity.effect;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.LightningEntity;

public class LightningRenderer extends EntityRenderer<LightningEntity> {
    private final BoltRenderer renderer;

    public LightningRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.renderer = new BoltRenderer();
    }

    @Override
    public void render(@NotNull LightningEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (!(pEntity.getOwner() instanceof LivingEntity owner)) return;

        Vector3f color = ParticleColors.getCursedEnergyColor(owner);

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2, 0.0F);

        double collidePosX = pEntity.collidePosX;
        double collidePosY = pEntity.collidePosY;
        double collidePosZ = pEntity.collidePosZ;

        Vec3 start = new Vec3(pEntity.getX(), pEntity.getY(), pEntity.getZ());
        Vec3 end = new Vec3(collidePosX, collidePosY, collidePosZ);
        BoltEffect.BoltRenderInfo info = new BoltEffect.BoltRenderInfo(1.0F, 0.1F, 0.1F, 0.1F,
                new Vector4f(color.x, color.y, color.z, 0.8F), 1.4F);
        BoltEffect bolt = new BoltEffect(info, start, end, (int) (Math.sqrt(start.distanceTo(end))) * 10)
                .size(0.05F)
                .lifespan(1)
                .fade(BoltEffect.FadeFunction.NONE)
                .spawn(BoltEffect.SpawnFunction.CONSECUTIVE);
        this.renderer.update(null, bolt, pPartialTick);
        pPoseStack.translate(-pEntity.getX(), -pEntity.getY(), -pEntity.getZ());
        this.renderer.render(pPartialTick, pPoseStack, pBuffer);

        pPoseStack.popPose();
    }

    @Override
    protected int getBlockLightLevel(@NotNull LightningEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LightningEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
