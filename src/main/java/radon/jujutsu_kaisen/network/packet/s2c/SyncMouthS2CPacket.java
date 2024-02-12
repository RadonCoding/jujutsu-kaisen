package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.visual.visual.PerfectBodyVisual;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncMouthS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_mouth_clientbound");

    private final UUID src;

    public SyncMouthS2CPacket(UUID src) {
        this.src = src;
    }

    public SyncMouthS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> PerfectBodyVisual.onChant(this.src));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(this.src);
    }

    @Override
    public ResourceLocation id() {
        return IDENTIFIER;
    }
}
