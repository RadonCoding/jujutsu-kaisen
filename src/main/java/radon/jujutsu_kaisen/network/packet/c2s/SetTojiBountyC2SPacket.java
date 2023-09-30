package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import radon.jujutsu_kaisen.entity.sorcerer.TojiFushiguroEntity;
import radon.jujutsu_kaisen.menu.BountyMenu;

import java.nio.charset.Charset;

public class SetTojiBountyC2SPacket {
    private final CharSequence target;

    public SetTojiBountyC2SPacket(CharSequence target) {
        this.target = target;
    }

    public SetTojiBountyC2SPacket(FriendlyByteBuf buf) {
        this(buf.readCharSequence(buf.readInt(), Charset.defaultCharset()));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.target.length());
        buf.writeCharSequence(this.target, Charset.defaultCharset());
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            if (sender.containerMenu instanceof BountyMenu menu) {
                if (!menu.stillValid(sender)) {
                    return;
                }

                if (menu.charge()) {
                    TojiFushiguroEntity entity = menu.getEntity();
                    ServerPlayer target = sender.server.getPlayerList().getPlayerByName(String.valueOf(this.target));

                    if (target != null && entity != null) {
                        entity.setBounty(sender, target);
                    }
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}