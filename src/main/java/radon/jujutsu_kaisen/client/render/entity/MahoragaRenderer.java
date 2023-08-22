package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.MahoragaSwordLayer;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MahoragaRenderer extends TenShadowsRenderer<MahoragaEntity> {
    public MahoragaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "mahoraga"), true));

        this.addRenderLayer(new MahoragaSwordLayer(this));
    }
}
