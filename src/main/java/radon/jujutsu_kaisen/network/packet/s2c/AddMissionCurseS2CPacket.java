package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;

import java.util.UUID;

public record AddMissionCurseS2CPacket(ResourceKey<Level> dimension, BlockPos pos, UUID identifier) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AddMissionCurseS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "add_mission_curse_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, AddMissionCurseS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            AddMissionCurseS2CPacket::dimension,
            BlockPos.STREAM_CODEC,
            AddMissionCurseS2CPacket::pos,
            UUIDUtil.STREAM_CODEC,
            AddMissionCurseS2CPacket::identifier,
            AddMissionCurseS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            if (player.level().dimension() != this.dimension) return;

            IMissionLevelData data = player.level().getData(JJKAttachmentTypes.MISSION_LEVEL);

            Mission mission = data.getMission(this.pos);

            if (mission == null) return;

            mission.addCurse(this.identifier);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
