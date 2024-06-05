package radon.jujutsu_kaisen.client.render.entity.ten_shadows;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.ToadWingsLayer;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.TenShadowsRenderer;
import radon.jujutsu_kaisen.entity.ten_shadows.ToadEntity;

public class ToadRenderer extends TenShadowsRenderer<ToadEntity> {
    public ToadRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad")));

        this.addRenderLayer(new ToadWingsLayer(this));
    }
}
