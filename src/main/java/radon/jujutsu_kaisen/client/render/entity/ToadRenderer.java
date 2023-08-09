package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.entity.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.ten_shadows.ToadEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ToadRenderer extends GeoEntityRenderer<ToadEntity> {
    public ToadRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad")));
    }
}
