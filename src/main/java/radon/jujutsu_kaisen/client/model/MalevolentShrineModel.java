package radon.jujutsu_kaisen.client.model;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.MalevolentShrineEntity;
import software.bernie.geckolib.model.GeoModel;

public class MalevolentShrineModel extends GeoModel<MalevolentShrineEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(JujutsuKaisen.MOD_ID, "geo/entity/malevolent_shrine.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/malevolent_shrine.png");

    @Override
    public ResourceLocation getModelResource(MalevolentShrineEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MalevolentShrineEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MalevolentShrineEntity animatable) {
        return null;
    }
}
