package radon.jujutsu_kaisen.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.OverlayDataHandler;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.RequestOverlayDataC2SPacket;

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
            Set<ResourceLocation> overlays;

            if (remote) {
                if (cap.isSynced(pLivingEntity.getUUID())) {
                    overlays = cap.getRemoteOverlays(pLivingEntity.getUUID());
                } else {
                    PacketHandler.sendToServer(new RequestOverlayDataC2SPacket(pLivingEntity.getUUID()));
                    return;
                }
            } else {
                overlays = cap.getLocalOverlays();
            }

            for (ResourceLocation overlay : overlays) {
                VertexConsumer consumer = pBuffer.getBuffer(JJKRenderTypes.glow(overlay));
                this.getParentModel().renderToBuffer(pMatrixStack, consumer, 0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        });
    }
}
