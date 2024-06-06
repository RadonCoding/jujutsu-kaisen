package radon.jujutsu_kaisen.client.render.entity.curse;


import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.RugbyFieldCurseEyesLayer;
import radon.jujutsu_kaisen.client.model.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.curse.RugbyFieldCurseEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RugbyFieldCurseRenderer extends GeoEntityRenderer<RugbyFieldCurseEntity> {
    public RugbyFieldCurseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "rugby_field_curse")));

        this.addRenderLayer(new RugbyFieldCurseEyesLayer(this));
    }
}
