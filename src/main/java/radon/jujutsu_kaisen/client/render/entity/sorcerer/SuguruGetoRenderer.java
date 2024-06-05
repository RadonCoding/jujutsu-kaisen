package radon.jujutsu_kaisen.client.render.entity.sorcerer;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.SuguruGetoModel;
import radon.jujutsu_kaisen.entity.sorcerer.SuguruGetoEntity;

public class SuguruGetoRenderer extends HumanoidMobRenderer<SuguruGetoEntity, SuguruGetoModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/suguru_geto.png");

    public SuguruGetoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SuguruGetoModel(pContext.bakeLayer(SuguruGetoModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(SuguruGetoModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(SuguruGetoModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SuguruGetoEntity pEntity) {
        return TEXTURE;
    }
}
