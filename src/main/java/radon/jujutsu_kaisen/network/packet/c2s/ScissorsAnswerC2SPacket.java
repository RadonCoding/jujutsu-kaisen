package radon.jujutsu_kaisen.network.packet.c2s;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;

import java.util.UUID;

public record ScissorsAnswerC2SPacket(UUID identifier) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ScissorsAnswerC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "scissors_answer_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ScissorsAnswerC2SPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ScissorsAnswerC2SPacket::identifier,
            ScissorsAnswerC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            ServerLevel level = sender.serverLevel();

            for (ScissorEntity scissor : level.getEntitiesOfClass(ScissorEntity.class, AABB.ofSize(sender.position(), 16.0D, 16.0D, 16.0D))) {
                Entity owner = scissor.getOwner();

                if (owner == null) continue;

                if (!owner.getUUID().equals(this.identifier)) continue;

                scissor.setActive(scissor.getTime());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}