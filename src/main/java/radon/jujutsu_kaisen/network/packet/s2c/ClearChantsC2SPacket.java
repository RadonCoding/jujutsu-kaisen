package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.client.chant.ClientChantHandler;

import java.util.function.Supplier;

public class ClearChantsC2SPacket {
    public ClearChantsC2SPacket() {
    }

    public ClearChantsC2SPacket(FriendlyByteBuf ignored) {
    }

    public void encode(FriendlyByteBuf ignored) {
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(ClientChantHandler::remove);
        ctx.setPacketHandled(true);
    }
}