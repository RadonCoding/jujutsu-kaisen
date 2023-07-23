package radon.jujutsu_kaisen.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

import javax.annotation.Nullable;

public class JJKBlocks {
    private static boolean never(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return false;
    }

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker) {
        return pClientType == pServerType ? (BlockEntityTicker<A>) pTicker : null;
    }

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<DomainBlock> UNLIMITED_VOID = BLOCKS.register("unlimited_void", () ->
            new DomainBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1.0F, 3600000.8F)
                    .isSuffocating(JJKBlocks::never)
                    .noLootTable()));

    public static RegistryObject<DomainBlock> COFFIN_OF_IRON_MOUNTAIN_ONE = BLOCKS.register("coffin_of_iron_mountain_one", () ->
            new DomainBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1.0F, 3600000.8F)
                    .isSuffocating(JJKBlocks::never)
                    .noLootTable()
                    .lightLevel((pState) -> 3)
                    .emissiveRendering((pState, pLevel, pPos) -> true)));

    public static RegistryObject<DomainBlock> COFFIN_OF_IRON_MOUNTAIN_TWO = BLOCKS.register("coffin_of_iron_mountain_two", () ->
            new DomainBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1.0F, 3600000.8F)
                    .isSuffocating(JJKBlocks::never)
                    .noLootTable()));

    public static RegistryObject<DomainBlock> COFFIN_OF_IRON_MOUNTAIN_THREE = BLOCKS.register("coffin_of_iron_mountain_three", () ->
            new DomainBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1.0F, 3600000.8F)
                    .isSuffocating(JJKBlocks::never)
                    .noLootTable()));
}
