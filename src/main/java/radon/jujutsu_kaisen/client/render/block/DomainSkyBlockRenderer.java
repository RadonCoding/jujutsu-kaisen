package radon.jujutsu_kaisen.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.block.entity.DomainSkyBlockEntity;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.render.domain.DomainRenderDispatcher;

public class DomainSkyBlockRenderer implements BlockEntityRenderer<DomainSkyBlockEntity> {

    public void render(@NotNull DomainSkyBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ResourceLocation domain = pBlockEntity.getDomain();

        if (domain == null) return;

        Matrix4f matrix4f = pPoseStack.last().pose();
        renderCube(matrix4f, pBuffer.getBuffer(renderType(domain)));
    }

    private static void renderCube(Matrix4f pPose, VertexConsumer pConsumer) {
        renderFace(pPose, pConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
        renderFace(pPose, pConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        renderFace(pPose, pConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        renderFace(pPose, pConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        renderFace(pPose, pConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
        renderFace(pPose, pConsumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F);
    }

    private static void renderFace(Matrix4f pPose, VertexConsumer pConsumer, float pX0, float pX1, float pY0, float pY1, float pZ0, float pZ1, float pZ2, float pZ3) {
        pConsumer.vertex(pPose, pX0, pY0, pZ0).endVertex();
        pConsumer.vertex(pPose, pX1, pY0, pZ1).endVertex();
        pConsumer.vertex(pPose, pX1, pY1, pZ2).endVertex();
        pConsumer.vertex(pPose, pX0, pY1, pZ3).endVertex();
    }

    private static RenderType renderType(ResourceLocation domain) {
        return JJKRenderTypes.skybox(DomainRenderDispatcher.get(domain));
    }
}
