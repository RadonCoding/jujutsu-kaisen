package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.projectile.TransfiguredSoulProjectile;

public class TransfiguredSoulRenderer extends ThrownItemRenderer<TransfiguredSoulProjectile> {
    public TransfiguredSoulRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, 1.0F, false);
    }

    @Override
    public void render(@NotNull TransfiguredSoulProjectile pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.XN.rotationDegrees(45.0F));

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);

        pPoseStack.popPose();
    }
}
