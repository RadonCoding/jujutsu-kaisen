package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.capability.data.projection_sorcery.ProjectionSorceryDataHandler;
import radon.jujutsu_kaisen.client.ClientWrapper;

import java.util.function.Supplier;

public class SyncProjectionSorceryDataS2CPacket {
    private final CompoundTag nbt;

    public SyncProjectionSorceryDataS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncProjectionSorceryDataS2CPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            IProjectionSorceryData cap = player.getCapability(ProjectionSorceryDataHandler.INSTANCE).resolve().orElseThrow();
            cap.deserializeNBT(this.nbt);
        });
        ctx.setPacketHandled(true);
    }
}
