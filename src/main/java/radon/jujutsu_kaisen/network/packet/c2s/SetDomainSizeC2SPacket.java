package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.function.Supplier;

public class SetDomainSizeC2SPacket {
    private final float domainSize;

    public SetDomainSizeC2SPacket(float domainSize) {
        this.domainSize = domainSize;
    }

    public SetDomainSizeC2SPacket(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.domainSize);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.setDomainSize(Mth.clamp(this.domainSize, ConfigHolder.SERVER.minimumDomainSize.get().floatValue(), ConfigHolder.SERVER.maximumDomainSize.get().floatValue()));
        });
        ctx.setPacketHandled(true);
    }
}