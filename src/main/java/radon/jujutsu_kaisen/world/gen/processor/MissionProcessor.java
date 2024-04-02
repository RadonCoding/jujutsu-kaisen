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
import radon.jujutsu_kaisen.block.CurseSpawnerBlock;
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

        if (pRelativeBlockInfo.state().is(JJKBlocks.CURSE_SPAWNER)) {
            RandomSource random = RandomSource.create(Mth.getSeed(pPos));

            if (!data.isRegistered(pPos)) data.register(HelperMethods.randomEnum(MissionType.class, random),
                    HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random), pPos);

            pRelativeBlockInfo.state().setValue(CurseSpawnerBlock.IS_BOSS, false);
            pRelativeBlockInfo.state().setValue(CurseSpawnerBlock.LOCATION, pPos.asLong());

            ((LevelAccessor) pLevel).scheduleTick(pRelativeBlockInfo.pos(), pRelativeBlockInfo.state().getBlock(), 0);
        } else if (pRelativeBlockInfo.state().is(JJKBlocks.CURSE_BOSS_SPAWNER)) {
            RandomSource random = RandomSource.create(Mth.getSeed(pPos));

            if (!data.isRegistered(pPos)) data.register(HelperMethods.randomEnum(MissionType.class, random),
                    HelperMethods.randomEnum(MissionGrade.class, Set.of(MissionGrade.S), random), pPos);

            pRelativeBlockInfo.state().setValue(CurseSpawnerBlock.IS_BOSS, true);
            pRelativeBlockInfo.state().setValue(CurseSpawnerBlock.LOCATION, pPos.asLong());

            ((LevelAccessor) pLevel).scheduleTick(pRelativeBlockInfo.pos(), pRelativeBlockInfo.state().getBlock(), 0);
        }
        return pRelativeBlockInfo;
    }
}
