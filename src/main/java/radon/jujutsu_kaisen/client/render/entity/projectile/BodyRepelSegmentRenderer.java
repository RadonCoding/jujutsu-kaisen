package radon.jujutsu_kaisen.client.render.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.render.entity.base.SegmentRenderer;
import radon.jujutsu_kaisen.entity.curse.WormCurseSegmentEntity;
import radon.jujutsu_kaisen.entity.projectile.BodyRepelSegmentEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BodyRepelSegmentRenderer extends SegmentRenderer<BodyRepelSegmentEntity> {
    public BodyRepelSegmentRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "body_repel_segment")));
    }
}