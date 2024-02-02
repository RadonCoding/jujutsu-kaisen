package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.client.visual.visual.PerfectBodyVisual;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncMouthS2CPacket {
    private final UUID src;

    public SyncMouthS2CPacket(UUID src) {
        this.src = src;
    }

    public SyncMouthS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.src);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (FMLLoader.getDist().isClient()) {
                PerfectBodyVisual.onChant(this.src);
            }
        });
        ctx.setPacketHandled(true);
    }
}
