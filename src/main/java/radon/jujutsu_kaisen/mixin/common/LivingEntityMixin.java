package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.IWings;
import radon.jujutsu_kaisen.ability.base.Transformation;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.entity.base.ISorcerer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    public void isFallFlying(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.level().isClientSide) {
            ClientVisualHandler.ClientData data = ClientVisualHandler.get((LivingEntity) (Object) this);

            if (data == null) return;

            for (Ability ability : data.toggled) {
                if (!(ability instanceof IWings wings)) continue;
                if (!wings.checkFlight(entity)) continue;

                cir.setReturnValue(true);
            }
        } else {
            if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            for (Ability ability : cap.getToggled()) {
                if (!(ability instanceof IWings wings)) continue;
                if (!wings.checkFlight(entity)) continue;

                cir.setReturnValue(true);
            }
        }
    }
}
