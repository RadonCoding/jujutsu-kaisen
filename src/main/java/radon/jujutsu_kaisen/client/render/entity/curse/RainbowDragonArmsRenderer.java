package radon.jujutsu_kaisen.client.render.entity.curse;


import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.render.entity.SegmentRenderer;
import radon.jujutsu_kaisen.entity.curse.RainbowDragonSegmentEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RainbowDragonArmsRenderer extends SegmentRenderer<RainbowDragonSegmentEntity> {
    public RainbowDragonArmsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "rainbow_dragon_arms")));
    }
}