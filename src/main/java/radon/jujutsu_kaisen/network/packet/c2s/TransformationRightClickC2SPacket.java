package radon.jujutsu_kaisen.network.packet.c2s;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.ITransformation;

public record TransformationRightClickC2SPacket(Ability ability) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TransformationRightClickC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "transformation_right_click_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, TransformationRightClickC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(JJKAbilities.ABILITY_KEY),
            TransformationRightClickC2SPacket::ability,
            TransformationRightClickC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (!(this.ability instanceof ITransformation transformation)) return;

            transformation.onRightClick(sender);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}