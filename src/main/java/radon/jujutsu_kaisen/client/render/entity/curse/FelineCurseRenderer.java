package radon.jujutsu_kaisen.client.render.entity.curse;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.curse.FelineCurseEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FelineCurseRenderer extends GeoEntityRenderer<FelineCurseEntity> {
    public FelineCurseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "feline_curse")));
    }
}
