package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.client.gui.overlay.SixEyesOverlay;

import java.util.UUID;
import java.util.function.Supplier;

public class ReceiveSixEyesDataS2CPacket {
    private final UUID src;
    private final CompoundTag nbt;

    public ReceiveSixEyesDataS2CPacket(UUID src, CompoundTag nbt) {
        this.src = src;
        this.nbt = nbt;
    }

    public ReceiveSixEyesDataS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.src);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            SixEyesOverlay.SixEyesData data = SixEyesOverlay.SixEyesData.deserializeNBT(this.nbt);
            SixEyesOverlay.setCurrent(this.src, data);
        }));
        ctx.setPacketHandled(true);
    }
}