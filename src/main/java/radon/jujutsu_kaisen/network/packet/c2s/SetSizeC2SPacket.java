package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.menu.VeilRodMenu;

public class SetSizeC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "set_size_serverbound");

    private final int size;

    public SetSizeC2SPacket(int size) {
        this.size = size;
    }

    public SetSizeC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void handle(ConfigurationPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            if (sender.containerMenu instanceof VeilRodMenu menu) {
                if (!menu.stillValid(sender)) {
                    return;
                }
                menu.setSize(Mth.clamp(this.size, ConfigHolder.SERVER.minimumVeilSize.get(), ConfigHolder.SERVER.maximumVeilSize.get()));
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.size);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}