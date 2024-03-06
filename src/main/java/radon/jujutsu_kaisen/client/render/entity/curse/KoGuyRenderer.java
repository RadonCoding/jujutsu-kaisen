package radon.jujutsu_kaisen.client.render.entity.curse;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.curse.CyclopsCurseEntity;
import radon.jujutsu_kaisen.entity.curse.KoGuyEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KoGuyRenderer extends GeoEntityRenderer<KoGuyEntity> {
    public KoGuyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "ko_guy")));
    }
}
