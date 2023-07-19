package radon.jujutsu_kaisen.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKBlocks {
    private static boolean never(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return false;
    }

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<DomainBlock> UNLIMITED_VOID = BLOCKS.register("unlimited_void", () ->
            new DomainBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1.0F, 3600000.8F)
                    .isSuffocating(JJKBlocks::never)
                    .noLootTable()));
    public static RegistryObject<DomainBlock> COFFIN_OF_IRON_MOUNTAIN = BLOCKS.register("coffin_of_iron_mountain", () ->
            new DomainBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1.0F, 3600000.8F)
                    .isSuffocating(JJKBlocks::never)
                    .noLootTable()
                    .lightLevel((pState) -> 3)
                    .emissiveRendering((pState, pLevel, pPos) -> true)));
}
