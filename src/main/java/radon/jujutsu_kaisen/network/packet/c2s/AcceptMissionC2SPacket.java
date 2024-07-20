package radon.jujutsu_kaisen.network.packet.c2s;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.DataProvider;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionLevelDataS2CPacket;
import radon.jujutsu_kaisen.tags.JJKStructureTags;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record AcceptMissionC2SPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AcceptMissionC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "accept_mission_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, AcceptMissionC2SPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            AcceptMissionC2SPacket::pos,
            AcceptMissionC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IMissionEntityData entityData = cap.getMissionData();

            if (entityData.getMission() != null) return;

            Optional<IMissionLevelData> levelData = DataProvider.getDataIfPresent(sender.level(), JJKAttachmentTypes.MISSION_LEVEL);

            if (levelData.isEmpty()) return;

            Mission mission = levelData.get().getMission(this.pos);

            if (mission == null) return;

            if (levelData.get().isTaken(mission)) return;

            entityData.setMission(mission);

            levelData.get().setTaken(mission, sender.getUUID());

            PacketDistributor.sendToAllPlayers(new SyncMissionLevelDataS2CPacket(sender.level().dimension(), levelData.get().serializeNBT(sender.registryAccess())));

            Vec3 pos = this.pos.getCenter();
            sender.teleportTo(pos.x, pos.y, pos.z);

            //PacketDistributor.sendToPlayer(sender, new FadeScreenTransitionS2CPacket(20));
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}