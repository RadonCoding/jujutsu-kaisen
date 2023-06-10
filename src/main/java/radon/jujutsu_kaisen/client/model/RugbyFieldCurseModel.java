package radon.jujutsu_kaisen.client.model;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.RugbyFieldCurseEntity;
import software.bernie.geckolib.model.GeoModel;

public class RugbyFieldCurseModel extends GeoModel<RugbyFieldCurseEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(JujutsuKaisen.MOD_ID, "geo/entity/rugby_field_curse.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/rugby_field_curse.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(JujutsuKaisen.MOD_ID, "animations/entity/rugby_field_curse.animation.json");

    @Override
    public ResourceLocation getModelResource(RugbyFieldCurseEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RugbyFieldCurseEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RugbyFieldCurseEntity animatable) {
        return ANIMATION;
    }
}