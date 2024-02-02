package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class ReceiveVisualDataS2CPacket {
    private final UUID src;
    private final CompoundTag nbt;

    public ReceiveVisualDataS2CPacket(UUID src, CompoundTag nbt) {
        this.src = src;
        this.nbt = nbt;
    }

    public ReceiveVisualDataS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.src);
        buf.writeNbt(this.nbt);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (FMLLoader.getDist().isClient()) {
                ClientVisualHandler.receive(this.src, this.nbt);
            }
        });
        ctx.setPacketHandled(true);
    }
}