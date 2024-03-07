package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.overlay.PerfectBodyOverlay;
import radon.jujutsu_kaisen.client.visual.visual.PerfectBodyVisual;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart rightArm;

    @Shadow public HumanoidModel.ArmPose rightArmPose;

    @Shadow public HumanoidModel.ArmPose leftArmPose;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
        ClientVisualHandler.ClientData client = ClientVisualHandler.get(pEntity);

        if (client == null) return;

        if (PerfectBodyOverlay.shouldRenderExtraArms(pEntity, client)) {
            if (this.rightArmPose == HumanoidModel.ArmPose.EMPTY || this.rightArmPose == HumanoidModel.ArmPose.ITEM) {
                this.rightArm.xRot += this.rightArm.xRot * 0.5F - (Mth.PI * 0.1F);
                this.rightArm.zRot -= this.rightArm.zRot * 0.5F - (Mth.PI * 0.1F);
            }
            if (this.leftArmPose == HumanoidModel.ArmPose.EMPTY || this.leftArmPose == HumanoidModel.ArmPose.ITEM) {
                this.leftArm.xRot += this.leftArm.xRot * 0.5F - (Mth.PI * 0.1F);
                this.leftArm.zRot += this.leftArm.zRot * 0.5F - (Mth.PI * 0.1F);
            }
        }
    }
}
