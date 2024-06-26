package radon.jujutsu_kaisen.mixin.common;


import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin {
    @Shadow
    @Final
    protected PathfinderMob mob;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void canUse(CallbackInfoReturnable<Boolean> cir) {
        MobEffectInstance instance = this.mob.getEffect(JJKEffects.STUN);

        if (instance != null && instance.getAmplifier() > 0) cir.setReturnValue(false);
    }

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    public void canContinueToUse(CallbackInfoReturnable<Boolean> cir) {
        MobEffectInstance instance = this.mob.getEffect(JJKEffects.STUN);

        if (instance != null && instance.getAmplifier() > 0) cir.setReturnValue(false);
    }
}
