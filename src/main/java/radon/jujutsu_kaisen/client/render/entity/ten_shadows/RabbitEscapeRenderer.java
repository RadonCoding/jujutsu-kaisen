package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.ten_shadows.NueTotalityEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RabbitEscapeRenderer extends TenShadowsRenderer<RabbitEscapeEntity> {
    public RabbitEscapeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "rabbit_escape")));
    }
}
