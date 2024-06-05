package radon.jujutsu_kaisen.block;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.domain.*;
import radon.jujutsu_kaisen.block.fluid.JJKFluids;

import javax.annotation.Nullable;

public class JJKBlocks {
    private static boolean always(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return false;
    }

    private static boolean never(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return false;
    }

    private static Boolean never(BlockState pState, BlockGetter pLevel, BlockPos pPos, EntityType<?> pType) {
        return false;
    }

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker) {
        return pClientType == pServerType ? (BlockEntityTicker<A>) pTicker : null;
    }

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<Block, Block> METEOR = BLOCKS.register("meteor", () -> new MagmaBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.NETHER)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .lightLevel(pState -> 3)
            .hasPostProcess(JJKBlocks::always)
            .emissiveRendering(JJKBlocks::always)));

    public static DeferredHolder<Block, DomainBlock> DOMAIN = BLOCKS.register("domain", () ->
            new DomainBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0F, 12.0F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel(pState -> 14)
                    .noLootTable()
                    .sound(SoundType.GLASS)));
    public static DeferredHolder<Block, DomainFloorBlock> DOMAIN_FLOOR = BLOCKS.register("domain_floor", () ->
            new DomainFloorBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0F, 3600000.8F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel(pState -> 14)
                    .noLootTable()
                    .sound(SoundType.GLASS)
                    .noTerrainParticles()));
    public static DeferredHolder<Block, DomainAirBlock> DOMAIN_AIR = BLOCKS.register("domain_air", () ->
            new DomainAirBlock(BlockBehaviour.Properties.of()
                    .lightLevel(pState -> 14)
                    .noCollission()
                    .noLootTable()
                    .air()));
    public static DeferredHolder<Block, DomainTransparentBlock> DOMAIN_TRANSPARENT = BLOCKS.register("domain_transparent", () ->
            new DomainTransparentBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0F, 3600000.8F)
                    .isSuffocating(JJKBlocks::never)
                    .lightLevel(pState -> 14)
                    .noLootTable()));

    public static DeferredHolder<Block, ChimeraShadowGardenBlock> CHIMERA_SHADOW_GARDEN = BLOCKS.register("chimera_shadow_garden", () ->
            new ChimeraShadowGardenBlock(JJKFluids.CHIMERA_SHADOW_GARDEN_SOURCE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    public static DeferredHolder<Block, Block> VEIL = BLOCKS.register("veil", () ->
            new VeilBlock(BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(VeilBlock.COLOR).getMapColor())
                    .strength(100.0F, 4.0F)
                    .isViewBlocking(JJKBlocks::never)
                    .isSuffocating(JJKBlocks::never)
                    .isValidSpawn(JJKBlocks::never)
                    .noOcclusion()));
    public static DeferredHolder<Block, Block> VEIL_ROD = BLOCKS.register("veil_rod", () ->
            new VeilRodBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 4.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
    public static DeferredHolder<Block, Block> ALTAR = BLOCKS.register("altar", () ->
            new AltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 1200.0F)
                    .sound(SoundType.ANVIL)));
    public static DeferredHolder<Block, Block> MISSION = BLOCKS.register("mission", () ->
            new MissionBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .lightLevel(state -> 3)
                    .emissiveRendering(JJKBlocks::always)
                    .strength(50.0F, 1200.0F)));

    public static DeferredHolder<Block, Block> CURSE_SPAWNER = BLOCKS.register("curse_spawner", () ->
            new CurseSpawnerBlock(BlockBehaviour.Properties.of().noCollission()));
    public static DeferredHolder<Block, Block> CURSE_BOSS_SPAWNER = BLOCKS.register("curse_boss_spawner", () ->
            new CurseSpawnerBlock(BlockBehaviour.Properties.of().noCollission()));

    public static DeferredHolder<Block, FakeWaterDurationBlock> FAKE_WATER_DURATION = BLOCKS.register("fake_water_duration", () ->
            new FakeWaterDurationBlock(JJKFluids.FAKE_WATER_SOURCE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static DeferredHolder<Block, FakeWaterDomainBlock> FAKE_WATER_DOMAIN = BLOCKS.register("fake_water_domain", () ->
            new FakeWaterDomainBlock(JJKFluids.FAKE_WATER_SOURCE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    public static DeferredHolder<Block, FakeWoodBlock> FAKE_WOOD = BLOCKS.register("fake_wood", () ->
            new FakeWoodBlock(BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .isSuffocating(JJKBlocks::never)
                    .ignitedByLava()));
}
