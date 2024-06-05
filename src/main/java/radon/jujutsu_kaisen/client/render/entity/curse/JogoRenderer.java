package radon.jujutsu_kaisen.client.render.entity.curse;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JogoEyeLayer;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.curse.JogoEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JogoRenderer extends GeoEntityRenderer<JogoEntity> {
    public JogoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "jogo")));

        this.addRenderLayer(new JogoEyeLayer(this));
    }
}
