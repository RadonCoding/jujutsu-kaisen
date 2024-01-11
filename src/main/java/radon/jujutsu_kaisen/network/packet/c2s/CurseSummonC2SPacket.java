package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;

import java.util.function.Supplier;

public class CurseSummonC2SPacket {
    private final int index;

    public CurseSummonC2SPacket(int index) {
        this.index = index;
    }

    public CurseSummonC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.index);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            JJKAbilities.summonCurse(sender, this.index, true);
        });
        ctx.setPacketHandled(true);
    }
}