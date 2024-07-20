package radon.jujutsu_kaisen.network.packet.c2s;


import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
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

import java.util.*;

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

            IMissionLevelData data = sender.level().getData(JJKAttachmentTypes.MISSION_LEVEL);

            HolderSet.Named<Structure> holders = sender.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(JJKStructureTags.IS_MISSION).orElseThrow();

            Map<StructurePlacement, Set<Holder<Structure>>> placements = new Object2ObjectArrayMap<>();

            for (Holder<Structure> holder : holders) {
                for (StructurePlacement placement : sender.serverLevel().getChunkSource().getGeneratorState().getPlacementsForStructure(holder)) {
                    placements.computeIfAbsent(placement, ignored -> new ObjectArraySet<>()).add(holder);
                }
            }

            ChunkGenerator generator = sender.serverLevel().getChunkSource().getGenerator();

            Map<BlockPos, Holder<Structure>> structures = new HashMap<>();

            List<Map.Entry<StructurePlacement, Set<Holder<Structure>>>> randoms = new ArrayList<>(placements.size());

            for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : placements.entrySet()) {
                StructurePlacement placement = entry.getKey();

                if (placement instanceof ConcentricRingsStructurePlacement concentric) {
                    Pair<BlockPos, Holder<Structure>> pair = generator.getNearestGeneratedStructure(
                            entry.getValue(), sender.serverLevel(), sender.serverLevel().structureManager(),
                            sender.blockPosition(), true, concentric
                    );

                    if (pair == null) continue;

                    structures.put(pair.getFirst(), pair.getSecond());
                } else if (placement instanceof RandomSpreadStructurePlacement) {
                    randoms.add(entry);
                }
            }

            int i = SectionPos.blockToSectionCoord(sender.blockPosition().getX());
            int j = SectionPos.blockToSectionCoord(sender.blockPosition().getZ());

            int found = 0;

            for (int k = 0; k <= SEARCH_RADIUS; k++) {
                if (found >= LIMIT) break;

                for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : randoms) {
                    RandomSpreadStructurePlacement random = (RandomSpreadStructurePlacement) entry.getKey();
                    Pair<BlockPos, Holder<Structure>> pair = ChunkGenerator.getNearestGeneratedStructure(
                            entry.getValue(), sender.serverLevel(), sender.serverLevel().structureManager(),
                            i,
                            j,
                            k,
                            true,
                            sender.serverLevel().getChunkSource().getGeneratorState().getLevelSeed(),
                            random
                    );

                    if (pair == null) continue;

                    structures.put(pair.getFirst(), pair.getSecond());

                    found++;
                }
            }

            int registered = 0;

            for (Map.Entry<BlockPos, Holder<Structure>> entry : structures.entrySet()) {
                BlockPos pos = entry.getKey();

                if (data.isRegistered(pos)) continue;

                SectionPos section = SectionPos.of(new ChunkPos(pos), sender.level().getMinSection());
                StructureStart start = sender.serverLevel().structureManager().getStartForStructure(
                        section, entry.getValue().value(), sender.level().getChunk(section.x(), section.z(), ChunkStatus.STRUCTURE_STARTS)
                );

                if (start == null) continue;

                BoundingBox bounds = start.getPieces().getFirst().getBoundingBox();
                BlockPos center = new BlockPos(bounds.getCenter().getX(), bounds.minY(), bounds.getCenter().getZ());

                RandomSource random = RandomSource.create(Mth.getSeed(center));
                data.register(HelperMethods.randomEnum(MissionType.class, random),
                        HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random), center);

                registered++;
            }

            if (registered > 0) {
                PacketDistributor.sendToAllPlayers(new SyncMissionLevelDataS2CPacket(sender.level().dimension(), data.serializeNBT(sender.registryAccess())));
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}