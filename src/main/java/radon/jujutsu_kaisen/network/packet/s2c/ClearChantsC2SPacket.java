package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.chant.ClientChantHandler;

import java.util.function.Supplier;

public class ClearChantsC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "clear_chants_serverbound");

    public ClearChantsC2SPacket() {
    }

    public ClearChantsC2SPacket(FriendlyByteBuf ignored) {
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> ClientChantHandler.remove());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {

    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}