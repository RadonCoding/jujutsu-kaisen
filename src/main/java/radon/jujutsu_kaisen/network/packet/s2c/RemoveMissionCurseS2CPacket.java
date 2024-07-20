package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;

import java.util.UUID;

public record RemoveMissionCurseS2CPacket(ResourceKey<Level> dimension, BlockPos pos, UUID identifier) implements CustomPacketPayload {
    public static final Type<RemoveMissionCurseS2CPacket> TYPE = new Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "remove_mission_curse_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RemoveMissionCurseS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            RemoveMissionCurseS2CPacket::dimension,
            BlockPos.STREAM_CODEC,
            RemoveMissionCurseS2CPacket::pos,
            UUIDUtil.STREAM_CODEC,
            RemoveMissionCurseS2CPacket::identifier,
            RemoveMissionCurseS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            if (player.level().dimension() != this.dimension) return;

            IMissionLevelData data = player.level().getData(JJKAttachmentTypes.MISSION_LEVEL);

            Mission mission = data.getMission(this.pos);

            if (mission == null) return;

            mission.getCurses().remove(this.identifier);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
