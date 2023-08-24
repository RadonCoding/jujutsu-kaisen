package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogTotalityEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DivineDogTotalityRenderer extends TenShadowsRenderer<DivineDogTotalityEntity> {
    public DivineDogTotalityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_totality"), true));
    }
}
