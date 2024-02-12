package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;

public class ChangeOutputC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "change_output_serverbound");

    public static final int INCREASE = 1;
    public static final int DECREASE = -1;

    private final int direction;

    public ChangeOutputC2SPacket(int direction) {
        this.direction = direction;
    }

    public ChangeOutputC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            ISorcererData data = sender.getData(JJKAttachmentTypes.SORCERER);

            if (this.direction == INCREASE) {
                data.increaseOutput();
            } else {
                data.decreaseOutput();
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.direction);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}