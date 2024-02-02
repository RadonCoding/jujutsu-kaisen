package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.client.gui.screen.VeilRodScreen;

import java.util.function.Supplier;

public class SetFrequencyS2CPacket {
    private final int frequency;

    public SetFrequencyS2CPacket(int frequency) {
        this.frequency = frequency;
    }

    public SetFrequencyS2CPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.frequency);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (FMLLoader.getDist().isClient()) {
                Minecraft mc = Minecraft.getInstance();

                if (mc.screen instanceof VeilRodScreen screen) {
                    screen.setFrequency(this.frequency);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}