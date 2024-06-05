package radon.jujutsu_kaisen.client.render.entity.ten_shadows;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.ten_shadows.NueEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class NueRenderer extends TenShadowsRenderer<NueEntity> {
    public NueRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "nue")));
    }
}
