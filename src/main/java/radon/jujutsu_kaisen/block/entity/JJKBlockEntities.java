package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.entity.domain.SelfEmbodimentOfPerfectionEntity;

public class JJKBlockEntities {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<DomainBlockEntity>> DOMAIN = BLOCK_ENTITIES.register("domain", () ->
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

                            JJKBlocks.AUTHENTIC_MUTUAL_LOVE_ONE.get(),
                            JJKBlocks.AUTHENTIC_MUTUAL_LOVE_TWO.get(),
                            JJKBlocks.AUTHENTIC_MUTUAL_LOVE_THREE.get(),

                            JJKBlocks.CHIMERA_SHADOW_GARDEN.get())
                    .build(null));


    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<UnlimitedVoidBlockEntity>> UNLIMITED_VOID = BLOCK_ENTITIES.register("unlimited_void", () ->
            BlockEntityType.Builder.of(UnlimitedVoidBlockEntity::new,
                            JJKBlocks.UNLIMITED_VOID.get())
                    .build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<SelfEmbodimentOfPerfectionBlockEntity>> SELF_EMBODIMENT_OF_PERFECTION = BLOCK_ENTITIES.register("self_embodiment_of_perfection", () ->
            BlockEntityType.Builder.of(SelfEmbodimentOfPerfectionBlockEntity::new,
                            JJKBlocks.SELF_EMBODIMENT_OF_PERFECTION.get())
                    .build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<HorizonOTheCaptivatingSkandhaBlockEntity>> HORIZON_OF_THE_CAPTIVATING_SKANDHA = BLOCK_ENTITIES.register("horizon_of_the_captivating_skandha", () ->
            BlockEntityType.Builder.of(HorizonOTheCaptivatingSkandhaBlockEntity::new,
                            JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get())
                    .build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ShiningSeaOfFlowersBlockEntity>> SHINING_SEA_OF_FLOWERS = BLOCK_ENTITIES.register("shining_sea_of_flowers", () ->
            BlockEntityType.Builder.of(ShiningSeaOfFlowersBlockEntity::new,
                            JJKBlocks.SHINING_SEA_OF_FLOWERS.get())
                    .build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<AuthenticMutualLoveBlockEntity>> AUTHENTIC_MUTUAL_LOVE = BLOCK_ENTITIES.register("authentic_mutual_love", () ->
            BlockEntityType.Builder.of(AuthenticMutualLoveBlockEntity::new,
                            JJKBlocks.AUTHENTIC_MUTUAL_LOVE.get())
                    .build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<VeilRodBlockEntity>> VEIL_ROD = BLOCK_ENTITIES.register("veil_rod", () ->
            BlockEntityType.Builder.of(VeilRodBlockEntity::new,
                            JJKBlocks.VEIL_ROD.get())
                    .build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<VeilBlockEntity>> VEIL = BLOCK_ENTITIES.register("veil", () ->
            BlockEntityType.Builder.of(VeilBlockEntity::new,
                            JJKBlocks.VEIL.get())
                    .build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<DurationBlockEntity>> DURATION = BLOCK_ENTITIES.register("duration", () ->
            BlockEntityType.Builder.of(DurationBlockEntity::new,
                            JJKBlocks.FAKE_WATER_DURATION.get(),
                            JJKBlocks.FAKE_WOOD.get())
                    .build(null));
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MissionBlockEntity>> MISSION = BLOCK_ENTITIES.register("mission", () ->
            BlockEntityType.Builder.of(MissionBlockEntity::new,
                            JJKBlocks.MISSION.get())
                    .build(null));
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<CurseSpawnerBlockEntity>> CURSE_SPAWNER = BLOCK_ENTITIES.register("curse_spawner", () ->
            BlockEntityType.Builder.of(CurseSpawnerBlockEntity::new,
                            JJKBlocks.CURSE_SPAWNER.get(),
                            JJKBlocks.CURSE_BOSS_SPAWNER.get())
                    .build(null));
}
