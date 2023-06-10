package radon.jujutsu_kaisen.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import radon.jujutsu_kaisen.client.model.RugbyFieldCurseModel;
import radon.jujutsu_kaisen.entity.RugbyFieldCurseEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RugbyFieldCurseRenderer extends GeoEntityRenderer<RugbyFieldCurseEntity> {
    public RugbyFieldCurseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RugbyFieldCurseModel());
    }
}
