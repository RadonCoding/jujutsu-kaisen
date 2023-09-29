package radon.jujutsu_kaisen.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.fluid.JJKFluids;

import javax.annotation.Nullable;

public class JJKBlocks {
    private static boolean never(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return false;
    }
    private static boolean always(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker) {
        return pClientType == pServerType ? (BlockEntityTicker<A>) pTicker : null;
    }

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<DomainBlock> UNLIMITED_VOID = BLOCKS.register("unlimited_void", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel((pState) -> 14)
                    .noLootTable()));
    public static RegistryObject<DomainBlock> UNLIMITED_VOID_FILL = BLOCKS.register("unlimited_void_fill", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel((pState) -> 14)
                    .noLootTable()));

    public static RegistryObject<DomainBlock> COFFIN_OF_THE_IRON_MOUNTAIN_ONE = BLOCKS.register("coffin_of_the_iron_mountain_one", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .noLootTable()
                    .lightLevel((pState) -> 3)
                    .emissiveRendering((pState, pLevel, pPos) -> true)));
    public static RegistryObject<DomainBlock> COFFIN_OF_THE_IRON_MOUNTAIN_TWO = BLOCKS.register("coffin_of_the_iron_mountain_two", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .noLootTable()));
    public static RegistryObject<DomainBlock> COFFIN_OF_THE_IRON_MOUNTAIN_THREE = BLOCKS.register("coffin_of_the_iron_mountain_three", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .noLootTable()));

    public static RegistryObject<DomainBlock> HORIZON_OF_THE_CAPTIVATING_SKANDHA = BLOCKS.register("horizon_of_the_captivating_skandha", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel((pState) -> 14)
                    .noLootTable()));
    public static RegistryObject<DomainBlock> HORIZON_OF_THE_CAPTIVATING_SKANDHA_FILL = BLOCKS.register("horizon_of_the_captivating_skandha_fill", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel((pState) -> 14)
                    .noLootTable()));

    public static RegistryObject<DomainBlock> SHINING_SEA_OF_FLOWERS = BLOCKS.register("shining_sea_of_flowers", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel((pState) -> 14)
                    .noLootTable()));
    public static RegistryObject<DomainBlock> SHINING_SEA_OF_FLOWERS_FILL = BLOCKS.register("shining_sea_of_flowers_fill", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel((pState) -> 14)
                    .noLootTable()));
    public static RegistryObject<DomainBlock> SHINING_SEA_OF_FLOWERS_FLOOR = BLOCKS.register("shining_sea_of_flowers_floor", () ->
            new DomainBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .noCollission()
                    .strength(-1.0F, 8.0F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel((pState) -> 14)
                    .noLootTable()));

    public static RegistryObject<ChimeraShadowGardenBlock> CHIMERA_SHADOW_GARDEN = BLOCKS.register("chimera_shadow_garden", () ->
            new ChimeraShadowGardenBlock(JJKFluids.CHIMERA_SHADOW_GARDEN_SOURCE, BlockBehaviour.Properties.copy(Blocks.WATER)));

    public static RegistryObject<Block> DISPLAY_CASE = BLOCKS.register("display_case", () ->
            new DisplayCaseBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    public static RegistryObject<Block> ALTAR = BLOCKS.register("altar", () ->
            new AltarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 1200.0F)
                    .sound(SoundType.ANVIL)));

    public static RegistryObject<Block> VEIL_ROD = BLOCKS.register("veil_rod", () ->
            new VeilRodBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));

    public static RegistryObject<Block> VEIL = BLOCKS.register("veil", () ->
            new VeilBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(100.0F, 14.0F)
                    .isViewBlocking(JJKBlocks::always)
                    .noOcclusion()));

    public static RegistryObject<FakeWaterDurationBlock> FAKE_WATER_DURATION = BLOCKS.register("fake_water_duration", () ->
            new FakeWaterDurationBlock(JJKFluids.FAKE_WATER_SOURCE, BlockBehaviour.Properties.copy(Blocks.WATER)));
    public static RegistryObject<FakeWaterDomainBlock> FAKE_WATER_DOMAIN = BLOCKS.register("fake_water_domain", () ->
            new FakeWaterDomainBlock(JJKFluids.FAKE_WATER_SOURCE, BlockBehaviour.Properties.copy(Blocks.WATER)));

    public static RegistryObject<FakeWoodBlock> FAKE_WOOD = BLOCKS.register("fake_wood", () ->
            new FakeWoodBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
}
