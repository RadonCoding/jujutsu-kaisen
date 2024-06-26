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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.DataProvider;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.data.mission.MissionType;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionLevelDataS2CPacket;
import radon.jujutsu_kaisen.tags.JJKStructureTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SearchForMissionsC2SPacket implements CustomPacketPayload {
    public static final SearchForMissionsC2SPacket INSTANCE = new SearchForMissionsC2SPacket();
    public static final CustomPacketPayload.Type<SearchForMissionsC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "search_for_missions_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SearchForMissionsC2SPacket> STREAM_CODEC = StreamCodec.unit(
            INSTANCE
    );
    private static final int SEARCH_RADIUS = 8;
    private static final int LIMIT = 16;

    private SearchForMissionsC2SPacket() {
    }

    private static boolean tryAddReference(StructureManager pStructureManager, StructureStart pStructureStart) {
        if (pStructureStart.canBeReferenced()) {
            pStructureManager.addReference(pStructureStart);
            return true;
        } else {
            return false;
        }
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            Optional<IMissionLevelData> data = DataProvider.getDataIfPresent(sender.level(), JJKAttachmentTypes.MISSION_LEVEL);

            if (data.isEmpty()) return;

            Optional<HolderSet.Named<Structure>> optional = sender.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(JJKStructureTags.IS_MISSION);

            if (optional.isEmpty()) return;

            int found = 0;

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

                            if (sender.serverLevel().isLoaded(pos)) continue;

                            StructureCheckResult result = sender.serverLevel().structureManager().checkStructurePresence(chunk, holder.value(), concentric, true);

                            if (result == StructureCheckResult.START_NOT_PRESENT) continue;

                            ChunkAccess access = sender.level().getChunk(chunk.x, chunk.z, ChunkStatus.STRUCTURE_STARTS);
                            StructureStart start = sender.serverLevel().structureManager().getStartForStructure(SectionPos.bottomOf(access), holder.value(), access);

                            if (start == null) continue;

                            if (data.get().isRegistered(pos)) continue;

                            RandomSource random = RandomSource.create(Mth.getSeed(pos));
                            data.get().register(HelperMethods.randomEnum(MissionType.class, random),
                                    HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random),
                                    new BlockPos(pos.getX(), start.getBoundingBox().maxY(), pos.getZ()));
                            found++;
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

                                        if (sender.serverLevel().isLoaded(BlockPos.containing(l, 0, i1))) continue;

                                        ChunkPos chunk = spread.getPotentialStructureChunk(sender.serverLevel().getChunkSource().getGeneratorState().getLevelSeed(), l, i1);

                                        StructureCheckResult result = sender.serverLevel().structureManager().checkStructurePresence(chunk, holder.value(), spread, true);

                                        if (result == StructureCheckResult.START_NOT_PRESENT) continue;

                                        ChunkAccess access = sender.level().getChunk(chunk.x, chunk.z, ChunkStatus.STRUCTURE_STARTS);
                                        StructureStart start = sender.serverLevel().structureManager().getStartForStructure(SectionPos.bottomOf(access), holder.value(), access);

                                        if (start == null || !start.isValid() || !tryAddReference(sender.serverLevel().structureManager(), start))
                                            continue;

                                        BlockPos pos = spread.getLocatePos(start.getChunkPos());

                                        if (data.get().isRegistered(pos)) continue;

                                        RandomSource random = RandomSource.create(Mth.getSeed(pos));
                                        data.get().register(HelperMethods.randomEnum(MissionType.class, random),
                                                HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random),
                                                new BlockPos(pos.getX(), start.getBoundingBox().maxY(), pos.getZ()));
                                        found++;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (found > 0) {
                PacketDistributor.sendToAllPlayers(new SyncMissionLevelDataS2CPacket(sender.level().dimension(), data.get().serializeNBT(sender.registryAccess())));
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}