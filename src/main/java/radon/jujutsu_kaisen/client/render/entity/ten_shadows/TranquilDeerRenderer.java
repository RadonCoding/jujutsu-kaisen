package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.ten_shadows.TranquilDeerEntity;

public class TranquilDeerRenderer extends TenShadowsRenderer<TranquilDeerEntity> {
    public TranquilDeerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "tranquil_deer")));
    }
}
