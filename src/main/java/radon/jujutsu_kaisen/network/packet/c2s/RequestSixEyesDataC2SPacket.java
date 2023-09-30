package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.overlay.SixEyesOverlay;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ReceiveSixEyesDataS2CPacket;

import java.util.UUID;

public class RequestSixEyesDataC2SPacket {
    private final UUID src;

    public RequestSixEyesDataC2SPacket(UUID uuid) {
        this.src = uuid;
    }

    public RequestSixEyesDataC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.src);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            LivingEntity target = (LivingEntity) sender.serverLevel().getEntity(this.src);

            if (target != null) {
                target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    SixEyesOverlay.SixEyesData data = new SixEyesOverlay.SixEyesData(cap.getTechnique(), cap.getGrade(), cap.getEnergy(), cap.getMaxEnergy());
                    PacketHandler.sendToClient(new ReceiveSixEyesDataS2CPacket(this.src, data.serializeNBT()), sender);
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}