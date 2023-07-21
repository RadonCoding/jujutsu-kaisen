package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.item.PistolItem;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {
    @Inject(method = "setupAnim*", at = @At(value = "TAIL"))
    private void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
        HumanoidModel<T> model = (HumanoidModel<T>) (Object) this;

        if (pEntity.getMainHandItem().getItem() instanceof PistolItem) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.options.mainHand().get() == HumanoidArm.RIGHT) {
                model.rightArm.yRot = -0.1F + model.head.yRot;
                model.rightArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
            } else {
                model.leftArm.yRot = 0.1F + model.head.yRot;
                model.leftArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
            }

            if (model instanceof PlayerModel<T>) {
                ((PlayerModel<T>) model).rightSleeve.copyFrom(model.rightArm);
                ((PlayerModel<T>) model).leftSleeve.copyFrom(model.leftArm);
            }
        }
    }
}
