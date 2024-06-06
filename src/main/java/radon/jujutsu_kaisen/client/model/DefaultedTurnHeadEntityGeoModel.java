package radon.jujutsu_kaisen.client.model;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DefaultedTurnHeadEntityGeoModel<T extends GeoAnimatable> extends DefaultedEntityGeoModel<T> {
    public DefaultedTurnHeadEntityGeoModel(ResourceLocation assetSubpath) {
        super(assetSubpath);
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotY(data.netHeadYaw() * (Mth.PI / 180.0F));
            head.setRotX(data.headPitch() * (Mth.PI / 180.0F));
        }
    }
}
