package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ReceiveVisualDataS2CPacket;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class RequestVisualDataC2SPacket {
    private final UUID src;

    public RequestVisualDataC2SPacket(UUID uuid) {
        this.src = uuid;
    }

    public RequestVisualDataC2SPacket(FriendlyByteBuf buf) {
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
                target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    Set<CursedTechnique> techniques = new HashSet<>();

                    if (cap.getTechnique() != null) techniques.add(cap.getTechnique());
                    if (cap.getCurrentCopied() != null) techniques.add(cap.getCurrentCopied());
                    if (cap.getCurrentAbsorbed() != null) techniques.add(cap.getCurrentAbsorbed());
                    if (cap.getAdditional() != null) techniques.add(cap.getAdditional());

                    ClientVisualHandler.VisualData data = new ClientVisualHandler.VisualData(cap.getToggled(), cap.getTraits(), techniques, cap.getType());
                    PacketHandler.sendToClient(new ReceiveVisualDataS2CPacket(this.src, data.serializeNBT()), sender);
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}