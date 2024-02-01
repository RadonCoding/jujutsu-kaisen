package radon.jujutsu_kaisen.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.entity.MimicryKatanaEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class MimicryKatanaRenderer extends EntityRenderer<MimicryKatanaEntity> {
    private final ItemRenderer renderer;

    public MimicryKatanaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.renderer = pContext.getItemRenderer();
    }

    @Override
    public void render(MimicryKatanaEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F - pitch));

        this.renderer.renderStatic(pEntity.getItem().getDefaultInstance(), ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), pEntity.getId());
        pPoseStack.popPose();

        DomainExpansionEntity domain = pEntity.getDomain();

        if (domain == null) return;

        if (Minecraft.getInstance().player != domain.getOwner()) return;

        Component name = pEntity.getTechnique().getName();

        double d0 = this.entityRenderDispatcher.distanceToSqr(pEntity);

        if (ForgeHooksClient.isNameplateInRenderDistance(pEntity, d0)) {
            boolean flag = !pEntity.isDiscrete();
            float f = pEntity.getNameTagOffsetY();
            pPoseStack.pushPose();
            pPoseStack.translate(0.0F, f, 0.0F);
            pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            pPoseStack.scale(-0.0125F, -0.0125F, 0.0125F);
            Matrix4f matrix4f = pPoseStack.last().pose();
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int) (f1 * 255.0F) << 24;
            Font font = this.getFont();
            float f2 = (float) (-font.width(name) / 2);
            font.drawInBatch(name, f2, 0.0F, 553648127, false, matrix4f, pBuffer, flag ? Font.DisplayMode.NORMAL : Font.DisplayMode.NORMAL, j, pPackedLight);

            if (flag) {
                font.drawInBatch(name, f2, 0.0F, -1, false, matrix4f, pBuffer, Font.DisplayMode.NORMAL, 0, pPackedLight);
            }
            pPoseStack.popPose();
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MimicryKatanaEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
