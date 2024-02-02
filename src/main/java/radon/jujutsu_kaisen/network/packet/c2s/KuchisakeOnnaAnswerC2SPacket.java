package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;

import java.util.UUID;
import java.util.function.Supplier;

public class KuchisakeOnnaAnswerC2SPacket {
    private final UUID identifier;

    public KuchisakeOnnaAnswerC2SPacket(UUID identifier) {
        this.identifier = identifier;
    }

    public KuchisakeOnnaAnswerC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.identifier);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            if (sender == null) return;

            ServerLevel level = sender.serverLevel();

            if (level.getEntity(this.identifier) instanceof KuchisakeOnnaEntity curse) {
                curse.attack();
            }
        });
        ctx.setPacketHandled(true);
    }
}