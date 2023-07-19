package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.OverlayDataHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncOverlayDataLocalS2CPacket {
    private final UUID identifier;
    private final CompoundTag nbt;

    public SyncOverlayDataLocalS2CPacket(UUID identifier, CompoundTag nbt) {
        this.identifier = identifier;
        this.nbt = nbt;
    }

    public SyncOverlayDataLocalS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.identifier);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            assert player != null;

            player.getCapability(OverlayDataHandler.INSTANCE).ifPresent(cap -> cap.deserializeLocalNBT(this.nbt));
        }));
        ctx.setPacketHandled(true);
    }
}