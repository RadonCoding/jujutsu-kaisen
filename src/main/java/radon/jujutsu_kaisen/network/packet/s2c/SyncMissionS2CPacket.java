package radon.jujutsu_kaisen.network.packet.s2c;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

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
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;

public record SyncMissionS2CPacket(ResourceKey<Level> dimension, CompoundTag nbt) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncMissionS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_mission_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncMissionS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            SyncMissionS2CPacket::dimension,
            ByteBufCodecs.COMPOUND_TAG,
            SyncMissionS2CPacket::nbt,
            SyncMissionS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            if (player.level().dimension() != this.dimension) return;

            IMissionLevelData data = player.level().getData(JJKAttachmentTypes.MISSION_LEVEL);

            Mission mission = new Mission(this.nbt);

            data.register(mission);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
