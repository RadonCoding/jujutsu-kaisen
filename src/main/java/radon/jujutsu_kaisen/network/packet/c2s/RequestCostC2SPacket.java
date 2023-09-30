package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.event.network.CustomPayloadEvent;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.menu.BountyMenu;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetCostS2CPacket;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class RequestCostC2SPacket {
    private final CharSequence name;

    public RequestCostC2SPacket(CharSequence name) {
        this.name = name;
    }

    public RequestCostC2SPacket(FriendlyByteBuf buf) {
        this(buf.readCharSequence(buf.readInt(), Charset.defaultCharset()));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.name.length());
        buf.writeCharSequence(this.name, Charset.defaultCharset());
    }

    public void handle(CustomPayloadEvent.Context ctx) {

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            ServerPlayer target = player.server.getPlayerList().getPlayerByName(String.valueOf(this.name));

            if (target != null) {
                target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    int cost = (Mth.floor(64 * ((float) (cap.getGrade().ordinal() + 1) / SorcererGrade.values().length)));

                    if (player.containerMenu instanceof BountyMenu menu) {
                        menu.setCost(cost);
                    }
                    PacketHandler.sendToClient(new SetCostS2CPacket(cost), player);
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}