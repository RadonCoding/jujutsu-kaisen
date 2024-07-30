package radon.jujutsu_kaisen.data.domain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.IClosedDomain;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.LinkedHashSet;
import java.util.List;

public class DomainCarver {
    private final LinkedHashSet<DomainInfo> domains;

    public DomainCarver(LinkedHashSet<DomainInfo> domains) {
        this.domains = domains;
    }

    private static void setBlockIfRequired(ServerLevel level, BlockPos pos, Block block) {
        if (level.getBlockState(pos).is(block)) return;

        level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE);
    }

    public void tick(ServerLevel level) {
        int radius = ConfigHolder.SERVER.domainSize.getAsInt();

        BlockPos center = BlockPos.containing(0.0D, radius, 0.0D);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    BlockPos pos = center.offset(x, y, z);

                    if (distance <= radius && distance >= radius - 1) {
                        setBlockIfRequired(level, pos, Blocks.BARRIER);
                    }
                }
            }
        }

        if (this.domains.size() > 1) return;

        DomainExpansion domain = this.domains.getFirst().ability();

        if (!(domain instanceof IClosedDomain closed)) return;

        List<Block> floor = closed.getBlocks();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y < 0; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + z * z);

                    BlockPos pos = center.offset(x, y, z);

                    if (distance < radius - 1) {
                        Block block = floor.get(HelperMethods.RANDOM.nextInt(floor.size()));
                        setBlockIfRequired(level, pos, block);
                    }
                }
            }
        }
    }
}
