package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;

public class OpenMissionScreenS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "open_mission_screen_clientbound");

    public OpenMissionScreenS2CPacket() {
    }

    public OpenMissionScreenS2CPacket(FriendlyByteBuf ignored) {
    }


    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(ClientWrapper::openMissions);
    }

    @Override
    public void write(@NotNull FriendlyByteBuf pBuffer) {

    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}