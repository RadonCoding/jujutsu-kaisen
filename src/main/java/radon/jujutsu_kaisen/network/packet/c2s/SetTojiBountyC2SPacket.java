package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.sorcerer.TojiFushiguroEntity;
import radon.jujutsu_kaisen.menu.BountyMenu;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class SetTojiBountyC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "set_toji_bounty_serverbound");

    private final CharSequence target;

    public SetTojiBountyC2SPacket(CharSequence target) {
        this.target = target;
    }

    public SetTojiBountyC2SPacket(FriendlyByteBuf buf) {
        this(buf.readCharSequence(buf.readInt(), Charset.defaultCharset()));
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

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
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.target.length());
        pBuffer.writeCharSequence(this.target, Charset.defaultCharset());
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}