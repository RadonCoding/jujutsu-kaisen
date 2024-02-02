package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.CurseManipulationDataHandler;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.client.ClientWrapper;

import java.util.function.Supplier;

public class SyncCurseManipulationDataS2CPacket {
    private final CompoundTag nbt;

    public SyncCurseManipulationDataS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncCurseManipulationDataS2CPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            ICurseManipulationData cap = player.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();
            cap.deserializeNBT(this.nbt);
        });
        ctx.setPacketHandled(true);
    }
}
