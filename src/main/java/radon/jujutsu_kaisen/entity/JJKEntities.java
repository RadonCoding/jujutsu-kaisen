package radon.jujutsu_kaisen.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.curse.*;
import radon.jujutsu_kaisen.entity.effect.*;
import radon.jujutsu_kaisen.entity.projectile.*;
import radon.jujutsu_kaisen.entity.sorcerer.*;
import radon.jujutsu_kaisen.entity.ten_shadows.*;

public class JJKEntities {
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JujutsuKaisen.MOD_ID);

    public static RegistryObject<EntityType<RedProjectile>> RED = ENTITIES.register("red", () ->
            EntityType.Builder.<RedProjectile>of(RedProjectile::new, MobCategory.MISC)
                    .sized(0.15F, 0.15F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "red")
                            .toString()));
    public static RegistryObject<EntityType<BlueProjectile>> BLUE = ENTITIES.register("blue", () ->
            EntityType.Builder.<BlueProjectile>of(BlueProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "blue")
                            .toString()));
    public static RegistryObject<EntityType<MaximumBlueProjectile>> MAXIMUM_BLUE = ENTITIES.register("maximum_blue", () ->
            EntityType.Builder.<MaximumBlueProjectile>of(MaximumBlueProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "maximum_blue")
                            .toString()));
    public static RegistryObject<EntityType<HollowPurpleProjectile>> HOLLOW_PURPLE = ENTITIES.register("hollow_purple", () ->
            EntityType.Builder.<HollowPurpleProjectile>of(HollowPurpleProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hollow_purple")
                            .toString()));
    public static RegistryObject<EntityType<MaximumHollowPurpleProjectile>> MAXIMUM_PURPLE_HOLLOW = ENTITIES.register("maximum_hollow_purple", () ->
            EntityType.Builder.<MaximumHollowPurpleProjectile>of(MaximumHollowPurpleProjectile::new, MobCategory.MISC)
                    .sized(2.0F, 2.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "maximum_hollow_purple")
                            .toString()));
    public static RegistryObject<EntityType<ClosedDomainExpansionEntity>> CLOSED_DOMAIN_EXPANSION = ENTITIES.register("closed_domain_expansion", () ->
            EntityType.Builder.<ClosedDomainExpansionEntity>of(ClosedDomainExpansionEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "closed_domain_expansion")
                            .toString()));
    public static RegistryObject<EntityType<MalevolentShrineEntity>> MALEVOLENT_SHRINE = ENTITIES.register("malevolent_shrine", () ->
            EntityType.Builder.<MalevolentShrineEntity>of(MalevolentShrineEntity::new, MobCategory.MISC)
                    .sized(3.0F, 4.0F)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "malevolent_shrine")
                            .toString()));

    public static RegistryObject<EntityType<ChimeraShadowGardenEntity>> CHIMERA_SHADOW_GARDEN = ENTITIES.register("chimera_shadow_garden", () ->
            EntityType.Builder.<ChimeraShadowGardenEntity>of(ChimeraShadowGardenEntity::new, MobCategory.MISC)
                    .sized(3.0F, 4.0F)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "chimera_shadow_garden")
                            .toString()));
    public static RegistryObject<EntityType<RugbyFieldCurseEntity>> RUGBY_FIELD_CURSE = ENTITIES.register("rugby_field_curse", () ->
            EntityType.Builder.of(RugbyFieldCurseEntity::new, MobCategory.AMBIENT)
                    .sized(2.0F, 2.4F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rugby_field_curse")
                            .toString()));
    public static RegistryObject<EntityType<JogoEntity>> JOGO = ENTITIES.register("jogo", () ->
            EntityType.Builder.<JogoEntity>of(JogoEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "jogo")
                            .toString()));
    public static RegistryObject<EntityType<FishCurseEntity>> FISH_CURSE = ENTITIES.register("fish_curse", () ->
            EntityType.Builder.<FishCurseEntity>of(FishCurseEntity::new, MobCategory.AMBIENT)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fish_curse")
                            .toString()));
    public static RegistryObject<EntityType<CyclopsCurseEntity>> CYCLOPS_CURSE = ENTITIES.register("cyclops_curse", () ->
            EntityType.Builder.<CyclopsCurseEntity>of(CyclopsCurseEntity::new, MobCategory.AMBIENT)
                    .sized(2.0F, 6.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cyclops_curse")
                            .toString()));
    public static RegistryObject<EntityType<KuchisakeOnna>> KUCHISAKE_ONNA = ENTITIES.register("kuchisake_onna", () ->
            EntityType.Builder.<KuchisakeOnna>of(KuchisakeOnna::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "kuchisake_onna")
                            .toString()));
    public static RegistryObject<EntityType<ZombaCurseEntity>> ZOMBA_CURSE = ENTITIES.register("zomba_curse", () ->
            EntityType.Builder.<ZombaCurseEntity>of(ZombaCurseEntity::new, MobCategory.AMBIENT)
                    .sized(1.6F, 2.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "zomba_curse")
                            .toString()));
    public static RegistryObject<EntityType<WormCurseEntity>> WORM_CURSE = ENTITIES.register("worm_curse", () ->
            EntityType.Builder.<WormCurseEntity>of(WormCurseEntity::new, MobCategory.AMBIENT)
                    .sized(1.0F, 0.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "worm_curse")
                            .toString()));

    public static RegistryObject<EntityType<TojiFushiguroEntity>> TOJI_FUSHIGURO = ENTITIES.register("toji_fushiguro", () ->
            EntityType.Builder.<TojiFushiguroEntity>of(TojiFushiguroEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro")
                            .toString()));
    public static RegistryObject<EntityType<SukunaRyomenEntity>> SUKUNA_RYOMEN = ENTITIES.register("sukuna_ryomen", () ->
            EntityType.Builder.<SukunaRyomenEntity>of(SukunaRyomenEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "sukuna_ryomen")
                            .toString()));
    public static RegistryObject<EntityType<SaturoGojoEntity>> SATORU_GOJO = ENTITIES.register("satoru_gojo", () ->
            EntityType.Builder.<SaturoGojoEntity>of(SaturoGojoEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "satoru_gojo")
                            .toString()));
    public static RegistryObject<EntityType<YutaOkkotsuEntity>> YUTA_OKKOTSU = ENTITIES.register("yuta_okkotsu", () ->
            EntityType.Builder.<YutaOkkotsuEntity>of(YutaOkkotsuEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuta_okkotsu")
                            .toString()));
    public static RegistryObject<EntityType<MegumiFushiguroEntity>> MEGUMI_FUSHIGURO = ENTITIES.register("megumi_fushiguro", () ->
            EntityType.Builder.<MegumiFushiguroEntity>of(MegumiFushiguroEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "megumi_fushiguro")
                            .toString()));
    public static RegistryObject<EntityType<TojiZeninEntity>> TOJI_ZENIN = ENTITIES.register("toji_zenin", () ->
            EntityType.Builder.<TojiZeninEntity>of(TojiZeninEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_zenin")
                            .toString()));
    public static RegistryObject<EntityType<MegunaRyomenEntity>> MEGUNA_RYOMEN = ENTITIES.register("meguna_ryomen", () ->
            EntityType.Builder.<MegunaRyomenEntity>of(MegunaRyomenEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "meguna_ryomen")
                            .toString()));
    public static RegistryObject<EntityType<YujiItadoriEntity>> YUJI_ITADORI = ENTITIES.register("yuji_itadori", () ->
            EntityType.Builder.<YujiItadoriEntity>of(YujiItadoriEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuji_itadori")
                            .toString()));
    public static RegistryObject<EntityType<RikaEntity>> RIKA = ENTITIES.register("rika", () ->
            EntityType.Builder.<RikaEntity>of(RikaEntity::new, MobCategory.MISC)
                    .sized(1.4F, 3.875F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rika")
                            .toString()));

    public static RegistryObject<EntityType<DismantleProjectile>> DISMANTLE = ENTITIES.register("dismantle", () ->
            EntityType.Builder.<DismantleProjectile>of(DismantleProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "dismantle")
                            .toString()));
    public static RegistryObject<EntityType<FireArrowProjectile>> FIRE_ARROW = ENTITIES.register("fire_arrow", () ->
            EntityType.Builder.<FireArrowProjectile>of(FireArrowProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fire_arrow")
                            .toString()));
    public static RegistryObject<EntityType<PureLoveBeam>> PURE_LOVE = ENTITIES.register("pure_love", () ->
            EntityType.Builder.<PureLoveBeam>of(PureLoveBeam::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "pure_love")
                            .toString()));
    public static RegistryObject<EntityType<MaximumRedBeam>> MAXIMUM_RED = ENTITIES.register("maximum_red", () ->
            EntityType.Builder.<MaximumRedBeam>of(MaximumRedBeam::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "maximum_red")
                            .toString()));
    public static RegistryObject<EntityType<EmberInsectProjectile>> EMBER_INSECT = ENTITIES.register("ember_insect", () ->
            EntityType.Builder.<EmberInsectProjectile>of(EmberInsectProjectile::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "ember_insect")
                            .toString()));
    public static RegistryObject<EntityType<VolcanoEntity>> VOLCANO = ENTITIES.register("volcano", () ->
            EntityType.Builder.<VolcanoEntity>of(VolcanoEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "volcano")
                            .toString()));
    public static RegistryObject<EntityType<MeteorEntity>> METEOR = ENTITIES.register("meteor", () ->
            EntityType.Builder.<MeteorEntity>of(MeteorEntity::new, MobCategory.MISC)
                    .sized(MeteorEntity.SIZE * 2, MeteorEntity.SIZE * 2)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "meteor")
                            .toString()));
    public static RegistryObject<EntityType<ThrownChainItemProjectile>> THROWN_CHAIN_ITEM = ENTITIES.register("throw_chain_item", () ->
            EntityType.Builder.<ThrownChainItemProjectile>of(ThrownChainItemProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "throw_chain_item")
                            .toString()));
    public static RegistryObject<EntityType<ScissorEntity>> SCISSOR = ENTITIES.register("scissor", () ->
            EntityType.Builder.<ScissorEntity>of(ScissorEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "scissor")
                            .toString()));
    public static RegistryObject<EntityType<FireballProjectile>> FIREBALL = ENTITIES.register("fireball", () ->
            EntityType.Builder.<FireballProjectile>of(FireballProjectile::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fireball")
                            .toString()));
    public static RegistryObject<EntityType<PiercingWaterEntity>> PIERCING_WATER = ENTITIES.register("piercing_water", () ->
            EntityType.Builder.<PiercingWaterEntity>of(PiercingWaterEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "piercing_water")
                            .toString()));
    public static RegistryObject<EntityType<NueTotalityLightingEntity>> NUE_TOTALITY_LIGHTNING = ENTITIES.register("nue_totality_lightning", () ->
            EntityType.Builder.<NueTotalityLightingEntity>of(NueTotalityLightingEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "nue_totality_lightning")
                            .toString()));
    public static RegistryObject<EntityType<SkyStrikeEntity>> SKY_STRIKE = ENTITIES.register("sky_strike", () ->
            EntityType.Builder.<SkyStrikeEntity>of(SkyStrikeEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "sky_strike")
                            .toString()));

    public static RegistryObject<EntityType<WheelEntity>> WHEEL = ENTITIES.register("wheel", () ->
            EntityType.Builder.<WheelEntity>of(WheelEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "wheel")
                            .toString()));

    public static RegistryObject<EntityType<MahoragaEntity>> MAHORAGA = ENTITIES.register("mahoraga", () ->
            EntityType.Builder.<MahoragaEntity>of(MahoragaEntity::new, MobCategory.MISC)
                    .sized(1.4F, 3.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "mahoraga").toString()));
    public static RegistryObject<EntityType<DivineDogWhiteEntity>> DIVINE_DOG_WHITE = ENTITIES.register("divine_dog_white", () ->
            EntityType.Builder.<DivineDogWhiteEntity>of(DivineDogWhiteEntity::new, MobCategory.MISC)
                    .sized(1.6F, 1.7F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_white")
                            .toString()));
    public static RegistryObject<EntityType<DivineDogBlackEntity>> DIVINE_DOG_BLACK = ENTITIES.register("divine_dog_black", () ->
            EntityType.Builder.<DivineDogBlackEntity>of(DivineDogBlackEntity::new, MobCategory.MISC)
                    .sized(1.6F, 1.7F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_black")
                            .toString()));
    public static RegistryObject<EntityType<DivineDogTotalityEntity>> DIVINE_DOG_TOTALITY = ENTITIES.register("divine_dog_totality", () ->
            EntityType.Builder.<DivineDogTotalityEntity>of(DivineDogTotalityEntity::new, MobCategory.MISC)
                    .sized(1.6F, 2.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_totality")
                            .toString()));
    public static RegistryObject<EntityType<ToadEntity>> TOAD = ENTITIES.register("toad", () ->
            EntityType.Builder.<ToadEntity>of(ToadEntity::new, MobCategory.MISC)
                    .sized(1.6F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad")
                            .toString()));
    public static RegistryObject<EntityType<ToadTotalityEntity>> TOAD_TOTALITY = ENTITIES.register("toad_totality", () ->
            EntityType.Builder.<ToadTotalityEntity>of(ToadTotalityEntity::new, MobCategory.MISC)
                    .sized(1.6F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad_totality")
                            .toString()));
    public static RegistryObject<EntityType<ToadTongueProjectile>> TOAD_TONGUE = ENTITIES.register("toad_tongue", () ->
            EntityType.Builder.<ToadTongueProjectile>of(ToadTongueProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad_tongue")
                            .toString()));
    public static RegistryObject<EntityType<RabbitEscapeEntity>> RABBIT_ESCAPE = ENTITIES.register("rabbit_escape", () ->
            EntityType.Builder.<RabbitEscapeEntity>of(RabbitEscapeEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rabbit_escape")
                            .toString()));
    public static RegistryObject<EntityType<NueEntity>> NUE = ENTITIES.register("nue", () ->
            EntityType.Builder.<NueEntity>of(NueEntity::new, MobCategory.MISC)
                    .sized(1.4F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "nue")
                            .toString()));
    public static RegistryObject<EntityType<NueTotalityEntity>> NUE_TOTALITY = ENTITIES.register("nue_totality", () ->
            EntityType.Builder.<NueTotalityEntity>of(NueTotalityEntity::new, MobCategory.MISC)
                    .sized(5.6F, 7.2F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "nue_totality")
                            .toString()));
    public static RegistryObject<EntityType<GreatSerpentEntity>> GREAT_SERPENT = ENTITIES.register("great_serpent", () ->
            EntityType.Builder.<GreatSerpentEntity>of(GreatSerpentEntity::new, MobCategory.MISC)
                    .sized(1.0F, 0.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "great_serpent")
                            .toString()));
    public static RegistryObject<EntityType<MaxElephantEntity>> MAX_ELEPHANT = ENTITIES.register("max_elephant", () ->
            EntityType.Builder.<MaxElephantEntity>of(MaxElephantEntity::new, MobCategory.MISC)
                    .sized(3.8F, 3.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "max_elephant")
                            .toString()));
    public static RegistryObject<EntityType<TranquilDeerEntity>> TRANQUIL_DEER = ENTITIES.register("tranquil_deer", () ->
            EntityType.Builder.<TranquilDeerEntity>of(TranquilDeerEntity::new, MobCategory.MISC)
                    .sized(3.8F, 3.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "tranquil_deer")
                            .toString()));
    public static RegistryObject<EntityType<PiercingBullEntity>> PIERCING_BULL = ENTITIES.register("piercing_bull", () ->
            EntityType.Builder.<PiercingBullEntity>of(PiercingBullEntity::new, MobCategory.MISC)
                    .sized(2.0F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "piercing_bull")
                            .toString()));
    public static RegistryObject<EntityType<AgitoEntity>> AGITO = ENTITIES.register("agito", () ->
            EntityType.Builder.<AgitoEntity>of(AgitoEntity::new, MobCategory.MISC)
                    .sized(1.6F, 4.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "agito")
                            .toString()));

    public static void createAttributes(EntityAttributeCreationEvent event) {
        event.put(RUGBY_FIELD_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(TOJI_FUSHIGURO.get(), SorcererEntity.createAttributes().build());
        event.put(SUKUNA_RYOMEN.get(), SorcererEntity.createAttributes().build());
        event.put(SATORU_GOJO.get(), SorcererEntity.createAttributes().build());
        event.put(MEGUMI_FUSHIGURO.get(), SorcererEntity.createAttributes().build());
        event.put(YUTA_OKKOTSU.get(), SorcererEntity.createAttributes().build());
        event.put(TOJI_ZENIN.get(), SorcererEntity.createAttributes().build());
        event.put(MEGUNA_RYOMEN.get(), SorcererEntity.createAttributes().build());
        event.put(YUJI_ITADORI.get(), SorcererEntity.createAttributes().build());

        event.put(RIKA.get(), RikaEntity.createAttributes().build());
        event.put(JOGO.get(), SorcererEntity.createAttributes().build());
        event.put(MAHORAGA.get(), MahoragaEntity.createAttributes().build());
        event.put(DIVINE_DOG_WHITE.get(), DivineDogEntity.createAttributes().build());
        event.put(DIVINE_DOG_BLACK.get(), DivineDogEntity.createAttributes().build());
        event.put(DIVINE_DOG_TOTALITY.get(), DivineDogTotalityEntity.createAttributes().build());
        event.put(TOAD.get(), ToadEntity.createAttributes().build());
        event.put(TOAD_TOTALITY.get(), ToadEntity.createAttributes().build());
        event.put(RABBIT_ESCAPE.get(), RabbitEscapeEntity.createAttributes().build());
        event.put(NUE.get(), NueEntity.createAttributes().build());
        event.put(NUE_TOTALITY.get(), NueTotalityEntity.createAttributes().build());
        event.put(GREAT_SERPENT.get(), GreatSerpentEntity.createAttributes().build());
        event.put(MAX_ELEPHANT.get(), MaxElephantEntity.createAttributes().build());
        event.put(TRANQUIL_DEER.get(), TranquilDeerEntity.createAttributes().build());
        event.put(PIERCING_BULL.get(), PiercingBullEntity.createAttributes().build());
        event.put(AGITO.get(), AgitoEntity.createAttributes().build());

        event.put(FISH_CURSE.get(), FishCurseEntity.createAttributes().build());
        event.put(CYCLOPS_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(KUCHISAKE_ONNA.get(), KuchisakeOnna.createAttributes().build());
        event.put(ZOMBA_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(WORM_CURSE.get(), SorcererEntity.createAttributes().build());

        event.put(CLOSED_DOMAIN_EXPANSION.get(), Mob.createMobAttributes().build());
        event.put(MALEVOLENT_SHRINE.get(), Mob.createMobAttributes().build());
        event.put(CHIMERA_SHADOW_GARDEN.get(), Mob.createMobAttributes().build());
    }
}
