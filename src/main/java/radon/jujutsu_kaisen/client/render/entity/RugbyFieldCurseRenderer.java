package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import radon.jujutsu_kaisen.client.layer.RugbyFieldCurseEyesLayer;
import radon.jujutsu_kaisen.client.model.RugbyFieldCurseModel;
import radon.jujutsu_kaisen.entity.curse.RugbyFieldCurseEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RugbyFieldCurseRenderer extends GeoEntityRenderer<RugbyFieldCurseEntity> {
    public RugbyFieldCurseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RugbyFieldCurseModel());

        this.addRenderLayer(new RugbyFieldCurseEyesLayer(this));
    }
}
