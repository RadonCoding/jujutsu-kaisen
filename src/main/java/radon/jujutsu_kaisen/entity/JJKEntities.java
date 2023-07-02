package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.projectile.BlueProjectile;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.entity.projectile.HollowPurpleProjectile;
import radon.jujutsu_kaisen.entity.projectile.RedProjectile;

public class JJKEntities {
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JujutsuKaisen.MOD_ID);

    public static RegistryObject<EntityType<RedProjectile>> RED = ENTITIES.register("red", () ->
            EntityType.Builder.<RedProjectile>of(RedProjectile::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "red")
                            .toString()));
    public static RegistryObject<EntityType<BlueProjectile>> BLUE = ENTITIES.register("blue", () ->
            EntityType.Builder.<BlueProjectile>of(BlueProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "blue")
                            .toString()));

    public static RegistryObject<EntityType<HollowPurpleProjectile>> HOLLOW_PURPLE = ENTITIES.register("hollow_purple", () ->
            EntityType.Builder.<HollowPurpleProjectile>of(HollowPurpleProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hollow_purple")
                            .toString()));

    public static RegistryObject<EntityType<RugbyFieldCurseEntity>> RUGBY_FIELD_CURSE = ENTITIES.register("rugby_field_curse", () ->
            EntityType.Builder.of(RugbyFieldCurseEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rugby_field_curse")
                            .toString()));

    public static RegistryObject<EntityType<ClosedDomainExpansionEntity>> CLOSED_DOMAIN_EXPANSION = ENTITIES.register("closed_domain_expansion", () ->
            EntityType.Builder.<ClosedDomainExpansionEntity>of(ClosedDomainExpansionEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "closed_domain_expansion")
                            .toString()));

    public static RegistryObject<EntityType<MalevolentShrineEntity>> MALEVOLENT_SHRINE = ENTITIES.register("malevolent_shrine", () ->
            EntityType.Builder.<MalevolentShrineEntity>of(MalevolentShrineEntity::new, MobCategory.MISC)
                    .sized(3.0F, 4.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "malevolent_shrine")
                            .toString()));

    public static RegistryObject<EntityType<TojiFushiguroEntity>> TOJI_FUSHIGURO = ENTITIES.register("toji_fushiguro", () ->
            EntityType.Builder.<TojiFushiguroEntity>of(TojiFushiguroEntity::new, MobCategory.CREATURE)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro")
                            .toString()));

    public static RegistryObject<EntityType<SukunaRyomenEntity>> SUKUNA_RYOMEN = ENTITIES.register("sukuna_ryomen", () ->
            EntityType.Builder.<SukunaRyomenEntity>of(SukunaRyomenEntity::new, MobCategory.MONSTER)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "sukuna_ryomen")
                            .toString()));

    public static RegistryObject<EntityType<GojoSatoruEntity>> GOJO_SATORU = ENTITIES.register("gojo_satoru", () ->
            EntityType.Builder.<GojoSatoruEntity>of(GojoSatoruEntity::new, MobCategory.CREATURE)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "gojo_satoru")
                            .toString()));

    public static RegistryObject<EntityType<DismantleProjectile>> DISMANTLE = ENTITIES.register("dismantle", () ->
            EntityType.Builder.<DismantleProjectile>of(DismantleProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "dismantle")
                            .toString()));

    public static void createAttributes(EntityAttributeCreationEvent event) {
        event.put(RUGBY_FIELD_CURSE.get(), RugbyFieldCurseEntity.createAttributes().build());
        event.put(TOJI_FUSHIGURO.get(), TojiFushiguroEntity.createAttributes().build());
        event.put(SUKUNA_RYOMEN.get(), SukunaRyomenEntity.createAttributes().build());
        event.put(CLOSED_DOMAIN_EXPANSION.get(), ClosedDomainExpansionEntity.createMobAttributes().build());
        event.put(MALEVOLENT_SHRINE.get(), MalevolentShrineEntity.createMobAttributes().build());
        event.put(GOJO_SATORU.get(), GojoSatoruEntity.createMobAttributes().build());
    }

    private static boolean checkHostileSpawnRules(EntityType<? extends Mob> pType, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules(pType, pLevel, pSpawnType, pPos, pRandom);
    }

    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(RUGBY_FIELD_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                JJKEntities::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(TOJI_FUSHIGURO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                JJKEntities::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(SUKUNA_RYOMEN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                JJKEntities::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
