package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import radon.jujutsu_kaisen.client.model.MalevolentShrineModel;
import radon.jujutsu_kaisen.entity.MalevolentShrineEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MalevolentShrineRenderer extends GeoEntityRenderer<MalevolentShrineEntity> {
    public MalevolentShrineRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MalevolentShrineModel());
    }
}
