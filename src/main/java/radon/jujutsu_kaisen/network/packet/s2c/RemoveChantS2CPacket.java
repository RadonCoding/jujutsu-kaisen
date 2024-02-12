package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.chant.ClientChantHandler;

public class RemoveChantS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "remove_chant_clientbound");

    private final String chant;

    public RemoveChantS2CPacket(String chant) {
        this.chant = chant;
    }

    public RemoveChantS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> ClientChantHandler.remove(this.chant));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUtf(this.chant);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}