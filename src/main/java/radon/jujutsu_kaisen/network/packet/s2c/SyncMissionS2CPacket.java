package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;

import java.util.UUID;

public class SyncMissionS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_mission_clientbound");

    private final ResourceKey<Level> dimension;
    private final CompoundTag nbt;

    public SyncMissionS2CPacket(ResourceKey<Level> dimension, CompoundTag nbt) {
        this.dimension = dimension;
        this.nbt = nbt;
    }

    public SyncMissionS2CPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readNbt());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            if (player.level().dimension() != this.dimension) return;

            IMissionLevelData data = player.level().getData(JJKAttachmentTypes.MISSION_LEVEL);

            Mission mission = new Mission(this.nbt);

            data.register(mission);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceKey(this.dimension);
        pBuffer.writeNbt(this.nbt);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}
