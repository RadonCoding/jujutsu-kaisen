package radon.jujutsu_kaisen.client.visual.overlay;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IOverlay;

public class CursedSpeechOverlay implements IOverlay {
    private static final RenderType CURSED_SPEECH = RenderType.entityCutoutNoCull(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/cursed_speech.png"));

    @Override
    public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData client) {
        return client.techniques.contains(JJKCursedTechniques.CURSED_SPEECH.get());
    }

    @Override
    public <T extends LivingEntity> void render(T entity, ClientVisualHandler.ClientData client, ResourceLocation texture, EntityModel<T> model, PoseStack poseStack, MultiBufferSource buffer, float partialTicks, int packedLight) {
        VertexConsumer consumer = buffer.getBuffer(CURSED_SPEECH);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
