package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.MahoragaSwordLayer;
import radon.jujutsu_kaisen.client.model.entity.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MahoragaRenderer extends GeoEntityRenderer<MahoragaEntity> {
    public MahoragaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "mahoraga")));

        this.addRenderLayer(new MahoragaSwordLayer(this));
    }
}
