package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

import java.util.function.Supplier;

public class SetCursedEnergyColorC2SPacket {
    private final int cursedEnergyColor;

    public SetCursedEnergyColorC2SPacket(int cursedEnergyColor) {
        this.cursedEnergyColor = cursedEnergyColor;
    }

    public SetCursedEnergyColorC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.cursedEnergyColor);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.setCursedEnergyColor(this.cursedEnergyColor);
        });
        ctx.setPacketHandled(true);
    }
}