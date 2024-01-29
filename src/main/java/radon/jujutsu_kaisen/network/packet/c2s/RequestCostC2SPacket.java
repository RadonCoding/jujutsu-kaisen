package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.menu.BountyMenu;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetCostS2CPacket;
import radon.jujutsu_kaisen.util.SorcererUtil;

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

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            ServerPlayer target = sender.server.getPlayerList().getPlayerByName(String.valueOf(this.name));

            if (target != null) {
                target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    int cost = (Mth.floor(64 * ((float) (SorcererUtil.getGrade(cap.getExperience()).ordinal() + 1) / SorcererGrade.values().length)));

                    if (sender.containerMenu instanceof BountyMenu menu) {
                        menu.setCost(cost);
                    }
                    PacketHandler.sendToClient(new SetCostS2CPacket(cost), sender);
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}