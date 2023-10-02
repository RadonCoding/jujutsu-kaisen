package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;
import radon.jujutsu_kaisen.client.gui.overlay.ScreenFlashOverlay;

public class ScreenFlashS2CPacket {
    public ScreenFlashS2CPacket() {
    }

    public ScreenFlashS2CPacket(FriendlyByteBuf ignored) {
        this();
    }

    public void encode(FriendlyByteBuf ignored) {

    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ScreenFlashOverlay::flash));
        ctx.setPacketHandled(true);
    }
}