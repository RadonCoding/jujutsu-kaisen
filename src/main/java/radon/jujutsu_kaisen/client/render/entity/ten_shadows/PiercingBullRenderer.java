package radon.jujutsu_kaisen.client.render.entity.ten_shadows;


import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.ten_shadows.PiercingBullEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PiercingBullRenderer extends GeoEntityRenderer<PiercingBullEntity> {
    public PiercingBullRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "piercing_bull")));
    }
}
