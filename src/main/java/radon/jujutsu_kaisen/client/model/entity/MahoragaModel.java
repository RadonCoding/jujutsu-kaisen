package radon.jujutsu_kaisen.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.curse.MahoragaEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MahoragaModel extends DefaultedEntityGeoModel<MahoragaEntity> {
    //private static final int WHEEL_TURN_TIME = 20;

    public MahoragaModel() {
        super(new ResourceLocation(JujutsuKaisen.MOD_ID, "mahoraga"));
    }

    @Override
    public void setCustomAnimations(MahoragaEntity animatable, long instanceId, AnimationState<MahoragaEntity> animationState) {
        /*CoreGeoBone wheel = this.getBone("wheel").orElseThrow();

        float current = wheel.getRotY();
        float required = animatable.getWheelSpin() * -45.0F;

        if (current != required) {
            wheel.setRotY(current + (required / WHEEL_TURN_TIME));
        }*/

        CoreGeoBone head = this.getBone("head").orElseThrow();

        EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        head.setRotX(head.getInitialSnapshot().getRotX() + data.headPitch() * Mth.DEG_TO_RAD);
        head.setRotY(head.getInitialSnapshot().getRotY() + data.netHeadYaw() * Mth.DEG_TO_RAD);
    }
}