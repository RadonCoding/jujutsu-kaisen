package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.effect.JJKEffects;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void shouldRender(T pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ, CallbackInfoReturnable<Boolean> cir) {
        if (pLivingEntity instanceof LivingEntity target && target.hasEffect(JJKEffects.UNDETECTABLE.get())) {
            Entity viewer = Minecraft.getInstance().getCameraEntity();

            if (viewer != null && target != viewer) {
                Vec3 look = viewer.getLookAngle();
                Vec3 start = viewer.getEyePosition();
                Vec3 result = target.position().subtract(start);

                double angle = Math.acos(look.normalize().dot(result.normalize()));

                if (angle > 0.5D) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
