package radon.jujutsu_kaisen.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.JJKBlocks;

public class JJKBlockEntities {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, JujutsuKaisen.MOD_ID);

    public static RegistryObject<BlockEntityType<DomainBlockEntity>> DOMAIN = BLOCK_ENTITIES.register("domain", () ->
            BlockEntityType.Builder.of(DomainBlockEntity::new,
                            JJKBlocks.DOMAIN.get(),
                            JJKBlocks.DOMAIN_AIR.get(),

                            JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_ONE.get(),
                            JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_TWO.get(),
                            JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_THREE.get(),

                            JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA_FILL.get(),
                            JJKBlocks.FAKE_WATER_DOMAIN.get(),

                            JJKBlocks.SHINING_SEA_OF_FLOWERS_FILL.get(),
                            JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get(),
                            JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_ONE.get(),
                            JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_TWO.get(),
                            JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_THREE.get(),
                            JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_FOUR.get(),

                            JJKBlocks.ALL_ENCOMPASSING_UNEQUIVOCAL_LOVE_ONE.get(),
                            JJKBlocks.ALL_ENCOMPASSING_UNEQUIVOCAL_LOVE_TWO.get(),
                            JJKBlocks.ALL_ENCOMPASSING_UNEQUIVOCAL_LOVE_THREE.get(),

                            JJKBlocks.CHIMERA_SHADOW_GARDEN.get())
                    .build(null));


    public static RegistryObject<BlockEntityType<UnlimitedVoidBlockEntity>> UNLIMITED_VOID = BLOCK_ENTITIES.register("unlimited_void", () ->
            BlockEntityType.Builder.of(UnlimitedVoidBlockEntity::new,
                            JJKBlocks.UNLIMITED_VOID.get())
                    .build(null));

    public static RegistryObject<BlockEntityType<DaySkyBlockEntity>> DAY_SKY = BLOCK_ENTITIES.register("day_sky", () ->
            BlockEntityType.Builder.of(DaySkyBlockEntity::new,
                            JJKBlocks.DAY_SKY.get())
                    .build(null));

    public static RegistryObject<BlockEntityType<NightSkyBlockEntity>> NIGHT_SKY = BLOCK_ENTITIES.register("night_sky", () ->
            BlockEntityType.Builder.of(NightSkyBlockEntity::new,
                            JJKBlocks.NIGHT_SKY.get())
                    .build(null));

    public static RegistryObject<BlockEntityType<DisplayCaseBlockEntity>> DISPLAY_CASE = BLOCK_ENTITIES.register("display_case", () ->
            BlockEntityType.Builder.of(DisplayCaseBlockEntity::new,
                            JJKBlocks.DISPLAY_CASE.get())
                    .build(null));

    public static RegistryObject<BlockEntityType<VeilRodBlockEntity>> VEIL_ROD = BLOCK_ENTITIES.register("veil_rod", () ->
            BlockEntityType.Builder.of(VeilRodBlockEntity::new,
                            JJKBlocks.VEIL_ROD.get())
                    .build(null));

    public static RegistryObject<BlockEntityType<VeilBlockEntity>> VEIL = BLOCK_ENTITIES.register("veil", () ->
            BlockEntityType.Builder.of(VeilBlockEntity::new,
                            JJKBlocks.VEIL.get())
                    .build(null));

    public static RegistryObject<BlockEntityType<DurationBlockEntity>> DURATION = BLOCK_ENTITIES.register("duration", () ->
            BlockEntityType.Builder.of(DurationBlockEntity::new,
                            JJKBlocks.FAKE_WATER_DURATION.get(),
                            JJKBlocks.FAKE_WOOD.get())
                    .build(null));
}
