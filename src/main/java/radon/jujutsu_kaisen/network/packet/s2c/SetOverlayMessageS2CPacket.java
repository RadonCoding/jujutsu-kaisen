package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;

import java.util.function.Supplier;

public class SetOverlayMessageS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "set_overlay_message_clientbound");

    private final Component component;
    private final boolean animate;

    public SetOverlayMessageS2CPacket(Component component, boolean animate) {
        this.component = component;
        this.animate = animate;
    }

    public SetOverlayMessageS2CPacket(FriendlyByteBuf buf) {
        this(buf.readComponent(), buf.readBoolean());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> ClientWrapper.setOverlayMessage(this.component, this.animate));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeComponent(this.component);
        pBuffer.writeBoolean(this.animate);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}