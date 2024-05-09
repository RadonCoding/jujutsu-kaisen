package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionLevelDataS2CPacket;

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

            IMissionLevelData levelData = sender.level().getData(JJKAttachmentTypes.MISSION_LEVEL);

            Mission mission = levelData.getMission(this.pos);

            if (mission == null) return;

            if (levelData.isTaken(mission)) return;

            entityData.setMission(mission);

            levelData.setTaken(mission, sender.getUUID());

            PacketDistributor.sendToAllPlayers(new SyncMissionLevelDataS2CPacket(sender.level().dimension(), levelData.serializeNBT(sender.registryAccess())));

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