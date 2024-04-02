package radon.jujutsu_kaisen.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.Event;

public class StructurePlaceEvent extends Event {
    private final Level level;
    private final Structure structure;
    private final BlockPos pos;

    public StructurePlaceEvent(Level level, Structure structure, BlockPos pos) {
        this.level = level;
        this.structure = structure;
        this.pos = pos;
    }

    public Level getLevel() {
        return this.level;
    }

    public Structure getStructure() {
        return this.structure;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
