package radon.jujutsu_kaisen.network.packet.c2s;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.sorcerer.TojiFushiguroEntity;
import radon.jujutsu_kaisen.menu.BountyMenu;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public record SetTojiBountyC2SPacket(String target) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetTojiBountyC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "set_toji_bounty_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SetTojiBountyC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SetTojiBountyC2SPacket::target,
            SetTojiBountyC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

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
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}