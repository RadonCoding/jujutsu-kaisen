package radon.jujutsu_kaisen.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.OverlayDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class RequestOverlayDataC2SPacket {
    private final UUID src;

    public RequestOverlayDataC2SPacket(UUID uuid) {
        this.src = uuid;
    }

    public RequestOverlayDataC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.src);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            LivingEntity target = (LivingEntity) sender.getLevel().getEntity(this.src);

            if (target != null) {
                target.getCapability(OverlayDataHandler.INSTANCE).ifPresent(srcCap -> {
                    sender.getCapability(OverlayDataHandler.INSTANCE).ifPresent(dstCap -> dstCap.deserializeRemoteNBT(target.getUUID(), srcCap.serializeNBT()));
                    PacketHandler.sendToClient(new SyncOverlayDataRemoteS2CPacket(this.src, srcCap.serializeNBT()), sender);
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}