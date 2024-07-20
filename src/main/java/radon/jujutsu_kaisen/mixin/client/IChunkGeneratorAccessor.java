package radon.jujutsu_kaisen.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(ChunkGenerator.class)
public interface IChunkGeneratorAccessor {
    @Invoker
    Pair<BlockPos, Holder<Structure>> invokeGetNearestGeneratedStructure(
            Set<Holder<Structure>> pStructureHoldersSet,
            ServerLevel pLevel,
            StructureManager pStructureManager,
            BlockPos pPos,
            boolean pSkipKnownStructures,
            ConcentricRingsStructurePlacement pPlacement
    );
}
