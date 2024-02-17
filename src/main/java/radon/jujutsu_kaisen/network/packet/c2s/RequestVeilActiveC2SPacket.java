package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.menu.BountyMenu;
import radon.jujutsu_kaisen.menu.VeilRodMenu;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetBountyCostS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SetVeilActiveS2CPacket;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.nio.charset.Charset;

public class RequestVeilActiveC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "request_veil_active_serverbound");

    public RequestVeilActiveC2SPacket() {

    }

    public RequestVeilActiveC2SPacket(FriendlyByteBuf ignored) {

    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            if (sender.containerMenu instanceof VeilRodMenu menu) {
                PacketHandler.sendToClient(new SetVeilActiveS2CPacket(menu.isActive()), sender);
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