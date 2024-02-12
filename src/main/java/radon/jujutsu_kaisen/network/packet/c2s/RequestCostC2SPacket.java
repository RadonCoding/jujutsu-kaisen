package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.menu.BountyMenu;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetCostS2CPacket;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.nio.charset.Charset;

public class RequestCostC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "request_cost_serverbound");

    private final CharSequence name;

    public RequestCostC2SPacket(CharSequence name) {
        this.name = name;
    }

    public RequestCostC2SPacket(FriendlyByteBuf buf) {
        this(buf.readCharSequence(buf.readInt(), Charset.defaultCharset()));
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            ServerPlayer target = sender.server.getPlayerList().getPlayerByName(String.valueOf(this.name));

            if (target == null) return;

            ISorcererData data = target.getData(JJKAttachmentTypes.SORCERER);

            int cost = (Mth.floor(64 * ((float) (SorcererUtil.getGrade(data.getExperience()).ordinal() + 1) / SorcererGrade.values().length)));

            if (sender.containerMenu instanceof BountyMenu menu) {
                menu.setCost(cost);
            }
            PacketHandler.sendToClient(new SetCostS2CPacket(cost), sender);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.name.length());
        pBuffer.writeCharSequence(this.name, Charset.defaultCharset());
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}