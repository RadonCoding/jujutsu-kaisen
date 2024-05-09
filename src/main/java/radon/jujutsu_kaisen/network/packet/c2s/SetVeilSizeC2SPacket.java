package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.menu.VeilRodMenu;

public record SetVeilSizeC2SPacket(int size) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetVeilSizeC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "set_veil_size_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SetVeilSizeC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SetVeilSizeC2SPacket::size,
            SetVeilSizeC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (sender.containerMenu instanceof VeilRodMenu menu) {
                if (!menu.stillValid(sender)) {
                    return;
                }
                menu.setSize(Mth.clamp(this.size, ConfigHolder.SERVER.minimumVeilSize.get(), ConfigHolder.SERVER.maximumVeilSize.get()));
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}