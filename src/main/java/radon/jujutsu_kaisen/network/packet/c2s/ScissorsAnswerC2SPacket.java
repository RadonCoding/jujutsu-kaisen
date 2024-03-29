package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;

import java.util.UUID;

public class ScissorsAnswerC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "scissors_answer_serverbound");

    private final UUID identifier;

    public ScissorsAnswerC2SPacket(UUID identifier) {
        this.identifier = identifier;
    }

    public ScissorsAnswerC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            ServerLevel level = sender.serverLevel();

            for (ScissorEntity scissor : level.getEntitiesOfClass(ScissorEntity.class, AABB.ofSize(sender.position(), 16.0D, 16.0D, 16.0D))) {
                scissor.setActive(scissor.getTime());
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(this.identifier);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}