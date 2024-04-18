package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.overlay.ScreenFlashOverlay;

import java.util.function.Supplier;

public class ScreenFlashS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "screen_flash_clientbound");

    public ScreenFlashS2CPacket() {
    }

    public ScreenFlashS2CPacket(FriendlyByteBuf ignored) {
        this();
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(ScreenFlashOverlay::flash);
    }

    @Override
    public void write(@NotNull FriendlyByteBuf pBuffer) {

    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}