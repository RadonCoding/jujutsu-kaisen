package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.ScissorCurseOpenLayer;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnna;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScissorCurseRenderer extends GeoEntityRenderer<KuchisakeOnna> {
    public ScissorCurseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "kuchisake_onna")));

        this.addRenderLayer(new ScissorCurseOpenLayer(this));
    }
}
