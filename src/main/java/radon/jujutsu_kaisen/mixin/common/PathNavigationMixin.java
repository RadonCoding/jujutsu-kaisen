package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import radon.jujutsu_kaisen.effect.JJKEffects;

@Mixin(PathNavigation.class)
public class PathNavigationMixin {
    @Shadow
    @Final
    protected Mob mob;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/control/MoveControl;setWantedPosition(DDDD)V"))
    public void tick(MoveControl instance, double pX, double pY, double pZ, double pSpeed) {
        if (!this.mob.hasEffect(JJKEffects.UNLIMITED_VOID.get()) && !this.mob.hasEffect(JJKEffects.STUN.get())) {
            instance.setWantedPosition(pX, pY, pZ, pSpeed);
        } else {
            instance.setWantedPosition(this.mob.getX(), this.mob.getY(), this.mob.getZ(), pSpeed);
        }
    }
}
