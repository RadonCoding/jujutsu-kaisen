package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.LevelLoadStatusManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.DimensionManager;

@Mixin(LevelLoadStatusManager.class)
public class LevelLoadStatusManagerMixin {
    @Shadow @Final private ClientLevel level;

    @Inject(method = "levelReady", at = @At("HEAD"), cancellable = true)
    public void levelReady(CallbackInfoReturnable<Boolean> cir) {
        if (DimensionManager.isTemporary(this.level.dimension())) cir.setReturnValue(true);
    }
}
