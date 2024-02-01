package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.capability.data.projection_sorcery.ProjectionSorceryDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
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

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Player player = ClientWrapper.getPlayer();

            assert player != null;

            IProjectionSorceryData cap = player.getCapability(ProjectionSorceryDataHandler.INSTANCE).resolve().orElseThrow();
            cap.deserializeNBT(this.nbt);
        }));
        ctx.setPacketHandled(true);
    }
}
