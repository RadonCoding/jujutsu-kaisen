package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.projectile.TransfiguredSoulProjectile;

public class TransfiguredSoulRenderer extends EntityRenderer<TransfiguredSoulProjectile> {
    private final ItemRenderer itemRenderer;

    public TransfiguredSoulRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.itemRenderer = pContext.getItemRenderer();
    }

    public void render(TransfiguredSoulProjectile pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        float yaw = Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YP.rotationDegrees(90.0F + yaw));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(135.0F - pitch));

        this.itemRenderer.renderStatic(pEntity.getItem(), ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), pEntity.getId());

        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TransfiguredSoulProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
