package radon.jujutsu_kaisen.world.gen.processor;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilRodBlock;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.mission.IMissionData;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.data.mission.MissionType;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.Set;

public class MissionProcessor extends StructureProcessor {
    public static final Codec<MissionProcessor> CODEC = Codec.unit(MissionProcessor::new);

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JJKProcessors.MISSION_PROCESSOR.get();
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader pLevel, @NotNull BlockPos pOffset, @NotNull BlockPos pPos, StructureTemplate.@NotNull StructureBlockInfo pBlockInfo, StructureTemplate.@NotNull StructureBlockInfo pRelativeBlockInfo, @NotNull StructurePlaceSettings pSettings, @Nullable StructureTemplate template) {
        IMissionData data = ((ServerLevelAccessor) pLevel).getLevel().getData(JJKAttachmentTypes.MISSION);

        if (data.isRegistered(pPos)) {
            Mission mission = data.getMission(pPos);

            if (mission.isFinalized()) return pRelativeBlockInfo;
        }

        if (pRelativeBlockInfo.state().is(JJKBlocks.CURSE_SPAWNER.get())) {
            RandomSource random = RandomSource.create(Mth.getSeed(pPos));

            if (!data.isRegistered(pPos)) data.register(HelperMethods.randomEnum(MissionType.class, random),
                    HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random), pPos);

            BlockPos pos = pRelativeBlockInfo.pos();

            Mission mission = data.getMission(pPos);
            mission.addSpawn(pos);

            return new StructureTemplate.StructureBlockInfo(pos, Blocks.AIR.defaultBlockState(), null);
        } else if (pRelativeBlockInfo.state().is(JJKBlocks.CURSE_BOSS_SPAWNER.get())) {
            RandomSource random = RandomSource.create(Mth.getSeed(pPos));

            if (!data.isRegistered(pPos)) data.register(HelperMethods.randomEnum(MissionType.class, random),
                    HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random), pPos);

            BlockPos pos = pRelativeBlockInfo.pos();

            Mission mission = data.getMission(pPos);
            mission.addBoss(pos);

            return new StructureTemplate.StructureBlockInfo(pos, Blocks.AIR.defaultBlockState(), null);
        }
        return pRelativeBlockInfo;
    }

    @Override
    public @NotNull List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor pServerLevel, @NotNull BlockPos pOffset, @NotNull BlockPos pPos, @NotNull List<StructureTemplate.StructureBlockInfo> pOriginalBlockInfos, @NotNull List<StructureTemplate.StructureBlockInfo> pProcessedBlockInfos, @NotNull StructurePlaceSettings pSettings) {
        IMissionData data = pServerLevel.getLevel().getData(JJKAttachmentTypes.MISSION);

        RandomSource random = RandomSource.create(Mth.getSeed(pPos));

        if (!data.isRegistered(pPos)) data.register(HelperMethods.randomEnum(MissionType.class, random),
                HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random), pPos);

        boolean finalized = true;

        for (StructureTemplate.StructureBlockInfo info : pProcessedBlockInfos) {
            if (!info.state().is(JJKBlocks.CURSE_SPAWNER) && !info.state().is(JJKBlocks.CURSE_BOSS_SPAWNER)) continue;

            finalized = false;
            break;
        }

        Mission mission = data.getMission(pPos);
        mission.setFinalized(finalized);

        return super.finalizeProcessing(pServerLevel, pOffset, pPos, pOriginalBlockInfos, pProcessedBlockInfos, pSettings);
    }
}
