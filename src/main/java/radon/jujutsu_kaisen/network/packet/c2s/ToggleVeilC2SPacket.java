package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.menu.VeilRodMenu;

public class ToggleVeilC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "toggle_veil_serverbound");

    private final boolean active;

    public ToggleVeilC2SPacket(boolean active) {
        this.active = active;
    }

    public ToggleVeilC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            if (sender.containerMenu instanceof VeilRodMenu menu) {
                if (!menu.stillValid(sender)) {
                    return;
                }
                menu.setActive(this.active);
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(this.active);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}