package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.client.gui.overlay.ScreenFlashOverlay;

import java.util.function.Supplier;

public class ScreenFlashS2CPacket {
    public ScreenFlashS2CPacket() {
    }

    public ScreenFlashS2CPacket(FriendlyByteBuf ignored) {
        this();
    }

    public void encode(FriendlyByteBuf ignored) {

    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(ScreenFlashOverlay::flash);
        ctx.setPacketHandled(true);
    }
}