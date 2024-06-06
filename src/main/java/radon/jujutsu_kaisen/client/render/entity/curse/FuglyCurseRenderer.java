package radon.jujutsu_kaisen.client.render.entity.curse;


import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.curse.FuglyCurseEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FuglyCurseRenderer extends GeoEntityRenderer<FuglyCurseEntity> {
    public FuglyCurseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "fugly_curse")));
    }
}
