package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.event.StructurePlaceEvent;

@Mixin(Structure.class)
public class StructureMixin {
    @Inject(method = "afterPlace", at = @At("HEAD"))
    public void afterPlace(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pChunkGenerator, RandomSource pRandom, BoundingBox pBoundingBox, ChunkPos pChunkPos, PiecesContainer pPieces, CallbackInfo ci) {
        BlockPos center = pBoundingBox.getCenter();
        BlockPos pos = new BlockPos(center.getX(), pBoundingBox.minY(), center.getZ());
        NeoForge.EVENT_BUS.post(new StructurePlaceEvent(pLevel.getLevel(), (Structure) (Object) this, pos));
    }
}
