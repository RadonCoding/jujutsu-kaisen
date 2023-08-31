package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.effect.JJKEffects;

@Mixin(Mob.class)
public class MobMixin {
    @Shadow protected MoveControl moveControl;

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    public void serverAiStep(CallbackInfo ci) {
        if (((LivingEntity) (Object) this).hasEffect(JJKEffects.UNLIMITED_VOID.get())) {
            ci.cancel();
        }
    }
}
