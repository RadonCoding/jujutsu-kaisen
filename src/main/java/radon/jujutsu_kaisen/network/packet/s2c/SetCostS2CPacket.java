package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.menu.BountyMenu;

import java.util.function.Supplier;

public class SetCostS2CPacket {
    private final int cost;

    public SetCostS2CPacket(int frequency) {
        this.cost = frequency;
    }

    public SetCostS2CPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.cost);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player.containerMenu instanceof BountyMenu menu) {
                menu.setCost(this.cost);
            }
        }));
        ctx.setPacketHandled(true);
    }
}