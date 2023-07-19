package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererData;
import radon.jujutsu_kaisen.client.gui.overlay.SixEyesOverlay;

import java.util.UUID;
import java.util.function.Supplier;

public class ReceiveSorcererDataS2CPacket {
    private final UUID src;
    private final CompoundTag nbt;

    public ReceiveSorcererDataS2CPacket(UUID src, CompoundTag nbt) {
        this.src = src;
        this.nbt = nbt;
    }

    public ReceiveSorcererDataS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.src);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ISorcererData data = new SorcererData();
            data.deserializeNBT(this.nbt);
            SixEyesOverlay.setCurrent(this.src, data);
        }));
        ctx.setPacketHandled(true);
    }
}