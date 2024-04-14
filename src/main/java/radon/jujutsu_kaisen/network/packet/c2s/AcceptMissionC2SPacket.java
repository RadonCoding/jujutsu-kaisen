package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.binding_vow.BindingVow;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionLevelDataS2CPacket;

public class AcceptMissionC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "accept_mission_serverbound");

    private final BlockPos pos;

    public AcceptMissionC2SPacket(BlockPos pos) {
        this.pos = pos;
    }

    public AcceptMissionC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

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

            PacketHandler.broadcast(new SyncMissionLevelDataS2CPacket(sender.level().dimension(), levelData.serializeNBT()));

            Vec3 pos = this.pos.getCenter();
            sender.teleportTo(pos.x, pos.y, pos.z);

            //PacketHandler.sendToClient(new FadeScreenTransitionS2CPacket(20), sender);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(this.pos);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}