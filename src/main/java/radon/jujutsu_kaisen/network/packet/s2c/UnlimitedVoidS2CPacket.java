package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.client.gui.overlay.UnlimitedVoidOverlay;

import java.util.function.Supplier;

public class UnlimitedVoidS2CPacket {
    private final int duration;

    public UnlimitedVoidS2CPacket(int duration) {
        this.duration = duration;
    }

    public UnlimitedVoidS2CPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.duration);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                UnlimitedVoidOverlay.trigger(this.duration)));
        ctx.setPacketHandled(true);
    }
}
