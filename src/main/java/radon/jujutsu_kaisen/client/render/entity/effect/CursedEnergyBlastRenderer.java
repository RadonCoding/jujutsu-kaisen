package radon.jujutsu_kaisen.client.render.entity.effect;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.model.entity.effect.CursedEnergyBlastModel;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.CursedEnergyBlastEntity;

public class CursedEnergyBlastRenderer extends EntityRenderer<CursedEnergyBlastEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/cursed_energy_beam.png");

    private final CursedEnergyBlastModel model;

    public CursedEnergyBlastRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.model = new CursedEnergyBlastModel(pContext.bakeLayer(CursedEnergyBlastModel.LAYER));
    }

    @Override
    public void render(CursedEnergyBlastEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (!(pEntity.getOwner() instanceof LivingEntity owner)) return;

        Vector3f color = ParticleColors.getCursedEnergyColor(owner);

        float age = pEntity.getTime() + pPartialTick;
        float fraction = age / (float) CursedEnergyBlastEntity.DURATION;
        float opacity = (float) Math.max((1.0F - Math.pow(fraction, 4.0F)) * 0.4F, 0.00001F);

        pPoseStack.pushPose();

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());

        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        VertexConsumer consumer = pBuffer.getBuffer(JJKRenderTypes.energy(this.getTextureLocation(pEntity)));
        this.model.setupAnim(pEntity, 0.0F, 0.0F, age, 0.0F, 0.0F);
        this.model.renderToBuffer(pPoseStack, consumer, pPackedLight, OverlayTexture.NO_OVERLAY, color.x, color.y, color.z, opacity);

        pPoseStack.popPose();
    }

    @Override
    protected int getBlockLightLevel(@NotNull CursedEnergyBlastEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CursedEnergyBlastEntity pEntity) {
        return TEXTURE;
    }
}
