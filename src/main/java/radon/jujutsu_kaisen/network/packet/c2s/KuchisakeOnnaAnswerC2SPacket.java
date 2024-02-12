package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;

import java.util.UUID;
import java.util.function.Supplier;

public class KuchisakeOnnaAnswerC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "kuchisake_onna_answer_serverbound");

    private final UUID identifier;

    public KuchisakeOnnaAnswerC2SPacket(UUID identifier) {
        this.identifier = identifier;
    }

    public KuchisakeOnnaAnswerC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            ServerLevel level = sender.serverLevel();

            if (level.getEntity(this.identifier) instanceof KuchisakeOnnaEntity curse) {
                curse.attack();
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