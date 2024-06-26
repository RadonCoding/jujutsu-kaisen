package radon.jujutsu_kaisen.client.render.entity.ten_shadows;


import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RabbitEscapeRenderer extends TenShadowsRenderer<RabbitEscapeEntity> {
    public RabbitEscapeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "rabbit_escape")));
    }
}
