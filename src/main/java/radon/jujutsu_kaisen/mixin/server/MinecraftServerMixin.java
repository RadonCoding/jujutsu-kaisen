package radon.jujutsu_kaisen.mixin.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.DimensionManager;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Redirect(method = "stopServer", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerLevel;noSave:Z", opcode = Opcodes.PUTFIELD))
    public void noSave(ServerLevel instance, boolean value) {
        if (DimensionManager.isTemporary(instance)) {
            instance.noSave = false;
        }
    }
}
