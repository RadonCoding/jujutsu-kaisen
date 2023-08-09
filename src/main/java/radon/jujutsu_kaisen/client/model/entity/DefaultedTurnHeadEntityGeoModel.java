package radon.jujutsu_kaisen.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DefaultedTurnHeadEntityGeoModel<T extends Mob & GeoAnimatable> extends DefaultedEntityGeoModel<T> {
    public DefaultedTurnHeadEntityGeoModel(ResourceLocation assetSubpath) {
        super(assetSubpath);
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        if (animatable.isNoAi()) return;

        CoreGeoBone head = this.getBone("head").orElseThrow();

        EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        head.setRotX(head.getInitialSnapshot().getRotX() + data.headPitch() * Mth.DEG_TO_RAD);
        head.setRotY(head.getInitialSnapshot().getRotY() + data.netHeadYaw() * Mth.DEG_TO_RAD);
    }
}
