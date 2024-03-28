package radon.jujutsu_kaisen.network.packet.c2s;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.mission.IMissionData;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.data.mission.MissionType;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionDataS2CPacket;
import radon.jujutsu_kaisen.tags.JJKStructureTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SearchForMissionsC2SPacket implements CustomPacketPayload {
    private static final int SEARCH_RADIUS = 16;

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "search_for_missions_serverbound");

    public SearchForMissionsC2SPacket() {

    }

    public SearchForMissionsC2SPacket(FriendlyByteBuf buf) {
    }

    private static boolean tryAddReference(StructureManager pStructureManager, StructureStart pStructureStart) {
        if (pStructureStart.canBeReferenced()) {
            pStructureManager.addReference(pStructureStart);
            return true;
        } else {
            return false;
        }
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            IMissionData data = sender.level().getData(JJKAttachmentTypes.MISSION);

            Optional<HolderSet.Named<Structure>> optional = sender.level().registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(JJKStructureTags.IS_MISSION);

            boolean dirty = false;

            if (optional.isEmpty()) return;

            for (Holder<Structure> holder : optional.get()) {
                for (StructurePlacement placement : sender.serverLevel().getChunkSource().getGeneratorState().getPlacementsForStructure(holder)) {
                    if (placement instanceof ConcentricRingsStructurePlacement concentric) {
                        List<ChunkPos> positions = sender.serverLevel().getChunkSource().getGeneratorState().getRingPositionsFor(concentric);

                        if (positions == null) continue;

                        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

                        for (ChunkPos chunk : positions) {
                            pos.set(SectionPos.sectionToBlockCoord(chunk.x, 8), 32, SectionPos.sectionToBlockCoord(chunk.z, 8));

                            if (!data.isRegistered(pos)) {
                                data.register(pos);

                                dirty = true;
                            }
                        }
                    } else if (placement instanceof RandomSpreadStructurePlacement random) {
                        int x = SectionPos.blockToSectionCoord(sender.getX());
                        int z = SectionPos.blockToSectionCoord(sender.getZ());

                        for (int y = 0; y <= SEARCH_RADIUS; y++) {
                            int i = random.spacing();

                            for (int j = -y; j <= y; j++) {
                                boolean flag = j == -y || j == y;

                                for (int k = -y; k <= y; k++) {
                                    if (flag || (k == -y || k == y)) {
                                        int l = x + i * j;
                                        int i1 = z + i * k;
                                        ChunkPos chunk = random.getPotentialStructureChunk(sender.serverLevel().getChunkSource().getGeneratorState().getLevelSeed(), l, i1);

                                        StructureCheckResult result = sender.serverLevel().structureManager().checkStructurePresence(chunk, holder.value(), true);

                                        if (result != StructureCheckResult.START_NOT_PRESENT) {
                                            ChunkAccess access = sender.level().getChunk(chunk.x, chunk.z, ChunkStatus.STRUCTURE_STARTS);
                                            StructureStart start = sender.serverLevel().structureManager().getStartForStructure(SectionPos.bottomOf(access), holder.value(), access);

                                            if (start != null && start.isValid() && (tryAddReference(sender.serverLevel().structureManager(), start))) {
                                                BlockPos pos = random.getLocatePos(start.getChunkPos());

                                                if (!data.isRegistered(pos)) {
                                                    data.register(pos);

                                                    dirty = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (dirty) {
                PacketHandler.broadcast(new SyncMissionDataS2CPacket(sender.level().dimension(), data.serializeNBT()));
            }
        });
    }

    @Override
    public void write(@NotNull FriendlyByteBuf pBuffer) {

    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}