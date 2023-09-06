package radon.jujutsu_kaisen.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.OverlayDataHandler;
import radon.jujutsu_kaisen.client.layer.overlay.RenderableOverlay;
import radon.jujutsu_kaisen.client.layer.overlay.Overlay;

import java.util.Set;

public class JJKOverlayLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public JJKOverlayLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        boolean remote = pLivingEntity != mc.player;

        mc.player.getCapability(OverlayDataHandler.INSTANCE).ifPresent(cap -> {
            Set<Overlay> overlays;

            if (remote) {
                if (cap.isSynced(pLivingEntity.getUUID())) {
                    overlays = cap.getRemoteOverlays(pLivingEntity.getUUID());
                } else {
                    return;
                }
            } else {
                overlays = cap.getLocalOverlays();
            }

            for (Overlay overlay : overlays) {
                if (!(overlay instanceof RenderableOverlay renderable)) continue;
                VertexConsumer consumer = pBuffer.getBuffer(renderable.getRenderType());
                this.getParentModel().renderToBuffer(pMatrixStack, consumer, renderable.getPackedLight(), OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F);
            }
        });
    }
}
