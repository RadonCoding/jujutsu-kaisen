package radon.jujutsu_kaisen.ability;


import net.minecraft.world.level.block.Block;

import java.util.List;

public interface IClosedDomain {
    default List<Block> getBlocks() {
        return List.of();
    }
}
