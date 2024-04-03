package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.data.mission.MissionType;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionLevelDataS2CPacket;
import radon.jujutsu_kaisen.tags.JJKStructureTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SearchForMissionsC2SPacket implements CustomPacketPayload {
    private static final int SEARCH_RADIUS = 16;
    private static final int LIMIT = 16;

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "search_for_missions_serverbound");

    public SearchForMissionsC2SPacket() {

    }

    public SearchForMissionsC2SPacket(FriendlyByteBuf ignored) {
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

            IMissionLevelData data = sender.level().getData(JJKAttachmentTypes.MISSION_LEVEL);

            Optional<HolderSet.Named<Structure>> optional = sender.level().registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(JJKStructureTags.IS_MISSION);

            int found = 0;

            if (optional.isEmpty()) return;

            // Locate structures that have not been registered yet, IMPORTANT: already known structures are skipped
            for (Holder<Structure> holder : optional.get()) {
                if (found >= LIMIT) break;

                for (StructurePlacement placement : sender.serverLevel().getChunkSource().getGeneratorState().getPlacementsForStructure(holder)) {
                    if (found >= LIMIT) break;

                    if (placement instanceof ConcentricRingsStructurePlacement concentric) {
                        List<ChunkPos> positions = sender.serverLevel().getChunkSource().getGeneratorState().getRingPositionsFor(concentric);

                        if (positions == null) continue;

                        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

                        for (ChunkPos chunk : positions) {
                            if (found >= LIMIT) break;

                            pos.set(SectionPos.sectionToBlockCoord(chunk.x, 8), 32, SectionPos.sectionToBlockCoord(chunk.z, 8));

                            StructureCheckResult result = sender.serverLevel().structureManager().checkStructurePresence(chunk, holder.value(), true);

                            if (result != StructureCheckResult.START_NOT_PRESENT) {
                                if (!data.isRegistered(pos)) {
                                    RandomSource random = RandomSource.create(Mth.getSeed(pos));
                                    data.register(HelperMethods.randomEnum(MissionType.class, random),
                                            HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random), pos);
                                    found++;
                                }
                            }
                        }
                    } else if (placement instanceof RandomSpreadStructurePlacement spread) {
                        int x = SectionPos.blockToSectionCoord(sender.getX());
                        int z = SectionPos.blockToSectionCoord(sender.getZ());

                        for (int y = 0; y <= SEARCH_RADIUS; y++) {
                            if (found >= LIMIT) break;

                            int i = spread.spacing();

                            for (int j = -y; j <= y; j++) {
                                if (found >= LIMIT) break;

                                boolean flag = j == -y || j == y;

                                for (int k = -y; k <= y; k++) {
                                    if (found >= LIMIT) break;

                                    if (flag || (k == -y || k == y)) {
                                        int l = x + i * j;
                                        int i1 = z + i * k;
                                        ChunkPos chunk = spread.getPotentialStructureChunk(sender.serverLevel().getChunkSource().getGeneratorState().getLevelSeed(), l, i1);

                                        StructureCheckResult result = sender.serverLevel().structureManager().checkStructurePresence(chunk, holder.value(), true);

                                        if (result != StructureCheckResult.START_NOT_PRESENT) {
                                            ChunkAccess access = sender.level().getChunk(chunk.x, chunk.z, ChunkStatus.STRUCTURE_STARTS);
                                            StructureStart start = sender.serverLevel().structureManager().getStartForStructure(SectionPos.bottomOf(access), holder.value(), access);

                                            if (start != null && start.isValid() && tryAddReference(sender.serverLevel().structureManager(), start)) {
                                                BlockPos pos = spread.getLocatePos(start.getChunkPos());

                                                if (!data.isRegistered(pos)) {
                                                    RandomSource random = RandomSource.create(Mth.getSeed(pos));
                                                    data.register(HelperMethods.randomEnum(MissionType.class, random),
                                                            HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random), pos);
                                                    found++;
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

            if (found > 0) {
                PacketHandler.broadcast(new SyncMissionLevelDataS2CPacket(sender.level().dimension(), data.serializeNBT()));
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