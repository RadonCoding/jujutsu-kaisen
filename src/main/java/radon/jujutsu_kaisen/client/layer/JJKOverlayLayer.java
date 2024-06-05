package radon.jujutsu_kaisen.client.layer;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

public class JJKOverlayLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public JJKOverlayLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        ClientVisualHandler.renderOverlays(pLivingEntity, this.getTextureLocation(pLivingEntity), this.getParentModel(), pMatrixStack, pBuffer, pPartialTicks, pPackedLight);
    }
}