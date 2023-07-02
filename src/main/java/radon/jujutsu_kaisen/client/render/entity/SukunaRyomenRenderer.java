package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.SukunaRyomenModel;
import radon.jujutsu_kaisen.entity.SukunaRyomenEntity;

public class SukunaRyomenRenderer extends HumanoidMobRenderer<SukunaRyomenEntity, SukunaRyomenModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/sukuna_ryomen.png");

    public SukunaRyomenRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SukunaRyomenModel(pContext.bakeLayer(SukunaRyomenModel.LAYER)), 0.5F);

        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SukunaRyomenEntity pEntity) {
        return TEXTURE;
    }
}
