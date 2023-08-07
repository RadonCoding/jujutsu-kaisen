package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import radon.jujutsu_kaisen.client.layer.MahoragaSwordLayer;
import radon.jujutsu_kaisen.client.model.entity.MahoragaModel;
import radon.jujutsu_kaisen.entity.curse.MahoragaEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MahoragaRenderer extends GeoEntityRenderer<MahoragaEntity> {
    public MahoragaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MahoragaModel());

        this.addRenderLayer(new MahoragaSwordLayer(this));
    }
}
