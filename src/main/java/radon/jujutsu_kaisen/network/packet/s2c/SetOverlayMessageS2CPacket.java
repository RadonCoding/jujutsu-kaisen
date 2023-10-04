package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetOverlayMessageS2CPacket {
    private final Component component;
    private final boolean animate;

    public SetOverlayMessageS2CPacket(Component component, boolean animate) {
        this.component = component;
        this.animate = animate;
    }

    public SetOverlayMessageS2CPacket(FriendlyByteBuf buf) {
        this(buf.readComponent(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(this.component);
        buf.writeBoolean(this.animate);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            mc.gui.setOverlayMessage(this.component, this.animate);
        }));
        ctx.setPacketHandled(true);
    }
}