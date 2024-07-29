package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import radon.jujutsu_kaisen.DimensionManager;

import java.util.Iterator;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Redirect(method = "saveAllChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getAllLevels()Ljava/lang/Iterable;"))
    public Iterable<ServerLevel> saveAllChunks(MinecraftServer instance) {
        Iterable<ServerLevel> iterable = instance.getAllLevels();

        for (Iterator<ServerLevel> it = iterable.iterator(); it.hasNext(); ) {
            ServerLevel level = it.next();

            if (DimensionManager.isTemporary(level.dimension())) {
                it.remove();
            }
        }
        return iterable;
    }

    @Redirect(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getAllLevels()Ljava/lang/Iterable;"))
    public Iterable<ServerLevel> stopServer(MinecraftServer instance) {
        Iterable<ServerLevel> iterable = instance.getAllLevels();

        for (Iterator<ServerLevel> it = iterable.iterator(); it.hasNext(); ) {
            ServerLevel level = it.next();

            if (DimensionManager.isTemporary(level.dimension())) {
                it.remove();
            }
        }
        return iterable;
    }
}
