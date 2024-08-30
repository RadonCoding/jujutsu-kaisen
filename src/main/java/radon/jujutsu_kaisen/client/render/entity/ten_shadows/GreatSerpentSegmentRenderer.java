package radon.jujutsu_kaisen.client.render.entity.ten_shadows;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.render.entity.SegmentRenderer;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentSegmentEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GreatSerpentSegmentRenderer extends SegmentRenderer<GreatSerpentSegmentEntity> {
    public GreatSerpentSegmentRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "great_serpent_segment")));
    }
}
