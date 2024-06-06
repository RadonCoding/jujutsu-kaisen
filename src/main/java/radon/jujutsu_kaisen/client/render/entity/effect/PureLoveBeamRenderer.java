package radon.jujutsu_kaisen.client.render.entity.effect;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.PureLoveBeamEntity;

public class PureLoveBeamRenderer extends EntityRenderer<PureLoveBeamEntity> {
    public PureLoveBeamRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(PureLoveBeamEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.getTime() < pEntity.getCharge()) return;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PureLoveBeamEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
