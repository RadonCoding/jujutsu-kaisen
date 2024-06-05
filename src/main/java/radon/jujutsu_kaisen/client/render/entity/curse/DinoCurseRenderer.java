package radon.jujutsu_kaisen.client.render.entity.curse;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.DinoCurseEyesLayer;
import radon.jujutsu_kaisen.client.layer.DinoCurseFoliageLayer;
import radon.jujutsu_kaisen.client.layer.DinoCurseTeethLayer;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.curse.BirdCurseEntity;
import radon.jujutsu_kaisen.entity.curse.DinoCurseEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DinoCurseRenderer extends GeoEntityRenderer<DinoCurseEntity> {
    public DinoCurseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "dino_curse")));

        this.addRenderLayer(new DinoCurseEyesLayer(this));
        this.addRenderLayer(new DinoCurseTeethLayer(this));
        this.addRenderLayer(new DinoCurseFoliageLayer(this));
    }
}
