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
import radon.jujutsu_kaisen.client.model.entity.TojiFushiguroModel;
import radon.jujutsu_kaisen.client.model.entity.YutaOkkotsuModel;
import radon.jujutsu_kaisen.entity.sorcerer.YutaOkkotsuEntity;

public class YutaOkkotsuRenderer extends HumanoidMobRenderer<YutaOkkotsuEntity, YutaOkkotsuModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/yuta_okkotsu.png");

    public YutaOkkotsuRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new YutaOkkotsuModel(pContext.bakeLayer(YutaOkkotsuModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(YutaOkkotsuModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(YutaOkkotsuModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull YutaOkkotsuEntity pEntity) {
        return TEXTURE;
    }
}
