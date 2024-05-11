package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.HajimeKashimoModel;
import radon.jujutsu_kaisen.client.model.entity.YutaOkkotsuModel;
import radon.jujutsu_kaisen.entity.sorcerer.HajimeKashimoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.YutaOkkotsuEntity;

public class HajimeKashimoRenderer extends HumanoidMobRenderer<HajimeKashimoEntity, HajimeKashimoModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/hajime_kashimo.png");

    public HajimeKashimoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new HajimeKashimoModel(pContext.bakeLayer(HajimeKashimoModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(HajimeKashimoModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(HajimeKashimoModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HajimeKashimoEntity pEntity) {
        return TEXTURE;
    }
}
