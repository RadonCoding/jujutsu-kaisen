package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.menu.VeilRodMenu;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetVeilActiveS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SetVeilSizeS2CPacket;

public class RequestVeilSizeC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "request_veil_size_serverbound");

    public RequestVeilSizeC2SPacket() {

    }

    public RequestVeilSizeC2SPacket(FriendlyByteBuf ignored) {

    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            if (sender.containerMenu instanceof VeilRodMenu menu) {
                PacketHandler.sendToClient(new SetVeilSizeS2CPacket(menu.getSize()), sender);
            }
        });
    }

    @Override
    public void write(@NotNull FriendlyByteBuf pBuffer) {

    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}