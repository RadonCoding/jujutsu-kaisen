package radon.jujutsu_kaisen.client.render.entity.idle_transfiguration;


import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.idle_transfiguration.PolymorphicSoulIsomerEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PolymorphicSoulIsomerRenderer extends GeoEntityRenderer<PolymorphicSoulIsomerEntity> {
    public PolymorphicSoulIsomerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "polymorphic_soul_isomer")));
    }
}
