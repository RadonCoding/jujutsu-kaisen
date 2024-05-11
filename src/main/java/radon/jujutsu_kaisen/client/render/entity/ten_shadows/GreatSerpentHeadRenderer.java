package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GreatSerpentHeadRenderer extends TenShadowsRenderer<GreatSerpentEntity> {
    public GreatSerpentHeadRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "great_serpent_head")));
    }
}
