package radon.jujutsu_kaisen.mixin.server;

import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.UpdateMultipartS2CPacket;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "sendDirtyEntityData", at = @At("HEAD"))
    public void sendDirtyEntityData(CallbackInfo ci) {
        if (this.entity.isMultipartEntity()) {
            PacketHandler.sendTracking(new UpdateMultipartS2CPacket(this.entity), this.entity);
        }
    }
}
