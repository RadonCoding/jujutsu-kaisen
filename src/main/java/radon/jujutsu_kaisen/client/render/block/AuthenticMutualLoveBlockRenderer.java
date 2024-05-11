package radon.jujutsu_kaisen.client.render.block;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.block.entity.AuthenticMutualLoveBlockEntity;
import radon.jujutsu_kaisen.block.entity.ShiningSeaOfFlowersBlockEntity;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.SkyHandler;

public class AuthenticMutualLoveBlockRenderer implements BlockEntityRenderer<AuthenticMutualLoveBlockEntity> {
    public AuthenticMutualLoveBlockRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    public void render(@NotNull AuthenticMutualLoveBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        this.renderCube(matrix4f, pBuffer.getBuffer(this.renderType()));
    }

    private void renderCube(Matrix4f pPose, VertexConsumer pConsumer) {
        this.renderFace(pPose, pConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
        this.renderFace(pPose, pConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.renderFace(pPose, pConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        this.renderFace(pPose, pConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        this.renderFace(pPose, pConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
        this.renderFace(pPose, pConsumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F);
    }

    private void renderFace(Matrix4f pPose, VertexConsumer pConsumer, float pX0, float pX1, float pY0, float pY1, float pZ0, float pZ1, float pZ2, float pZ3) {
        pConsumer.vertex(pPose, pX0, pY0, pZ0).endVertex();
        pConsumer.vertex(pPose, pX1, pY0, pZ1).endVertex();
        pConsumer.vertex(pPose, pX1, pY1, pZ2).endVertex();
        pConsumer.vertex(pPose, pX0, pY1, pZ3).endVertex();
    }

    protected RenderType renderType() {
        return JJKRenderTypes.skybox(SkyHandler.getAuthenticMutualLoveTarget());
    }
}