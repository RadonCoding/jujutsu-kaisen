package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.capability.data.ISoulData;
import radon.jujutsu_kaisen.capability.data.SoulDataHandler;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "getMaxHealth", at = @At("TAIL"), cancellable = true)
    public void getMaxHealth(CallbackInfoReturnable<Float> cir) {
        ISoulData cap = ((Entity) (Object) this).getCapability(SoulDataHandler.INSTANCE).resolve().orElseThrow();
        cir.setReturnValue(cir.getReturnValueF() - cap.getDamage());
    }
}
