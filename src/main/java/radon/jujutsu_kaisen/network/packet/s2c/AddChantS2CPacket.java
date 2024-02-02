package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.client.chant.ClientChantHandler;

import java.util.function.Supplier;

public class AddChantS2CPacket {
    private final String chant;

    public AddChantS2CPacket(String chant) {
        this.chant = chant;
    }

    public AddChantS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.chant);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> ClientChantHandler.add(this.chant));
        ctx.setPacketHandled(true);
    }
}