package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;

public class OpenMissionScreenS2CPacket implements CustomPacketPayload {
    public static final OpenMissionScreenS2CPacket INSTANCE = new OpenMissionScreenS2CPacket();

    public static final CustomPacketPayload.Type<OpenMissionScreenS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "open_mission_screen_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, OpenMissionScreenS2CPacket> STREAM_CODEC = StreamCodec.unit(
            INSTANCE
    );

    private OpenMissionScreenS2CPacket() {
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(ClientWrapper::openMissions);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}