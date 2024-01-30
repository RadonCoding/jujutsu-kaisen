package radon.jujutsu_kaisen.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.curse.*;
import radon.jujutsu_kaisen.entity.effect.*;
import radon.jujutsu_kaisen.entity.effect.WoodSegmentEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.PolymorphicSoulIsomerEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulLargeEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulNormalEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulSmallEntity;
import radon.jujutsu_kaisen.entity.projectile.*;
import radon.jujutsu_kaisen.entity.sorcerer.*;
import radon.jujutsu_kaisen.entity.ten_shadows.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JJKEntities {
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JujutsuKaisen.MOD_ID);

    public static RegistryObject<EntityType<ClosedDomainExpansionEntity>> CLOSED_DOMAIN_EXPANSION = ENTITIES.register("closed_domain_expansion", () ->
            EntityType.Builder.<ClosedDomainExpansionEntity>of(ClosedDomainExpansionEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "closed_domain_expansion")
                            .toString()));
    public static RegistryObject<EntityType<MalevolentShrineEntity>> MALEVOLENT_SHRINE = ENTITIES.register("malevolent_shrine", () ->
            EntityType.Builder.<MalevolentShrineEntity>of(MalevolentShrineEntity::new, MobCategory.MISC)
                    .sized(8.4F, 9.6F)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "malevolent_shrine")
                            .toString()));

    public static RegistryObject<EntityType<ChimeraShadowGardenEntity>> CHIMERA_SHADOW_GARDEN = ENTITIES.register("chimera_shadow_garden", () ->
            EntityType.Builder.<ChimeraShadowGardenEntity>of(ChimeraShadowGardenEntity::new, MobCategory.MISC)
                    .sized(3.0F, 4.0F)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "chimera_shadow_garden")
                            .toString()));

    public static RegistryObject<EntityType<JogoEntity>> JOGO = ENTITIES.register("jogo", () ->
            EntityType.Builder.<JogoEntity>of(JogoEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 1.9F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "jogo")
                            .toString()));
    public static RegistryObject<EntityType<JogoatEntity>> JOGOAT = ENTITIES.register("jogoat", () ->
            EntityType.Builder.<JogoatEntity>of(JogoatEntity::new, MobCategory.CREATURE)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "jogoat")
                            .toString()));
    public static RegistryObject<EntityType<DagonEntity>> DAGON = ENTITIES.register("dagon", () ->
            EntityType.Builder.<DagonEntity>of(DagonEntity::new, MobCategory.CREATURE)
                    .sized(1.4F, 3.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "dagon")
                            .toString()));
    public static RegistryObject<EntityType<HanamiEntity>> HANAMI = ENTITIES.register("hanami", () ->
            EntityType.Builder.<HanamiEntity>of(HanamiEntity::new, MobCategory.CREATURE)
                    .sized(1.4F, 3.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hanami")
                            .toString()));

    public static RegistryObject<EntityType<RugbyFieldCurseEntity>> RUGBY_FIELD_CURSE = ENTITIES.register("rugby_field_curse", () ->
            EntityType.Builder.of(RugbyFieldCurseEntity::new, MobCategory.AMBIENT)
                    .sized(2.0F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rugby_field_curse")
                            .toString()));
    public static RegistryObject<EntityType<FishCurseEntity>> FISH_CURSE = ENTITIES.register("fish_curse", () ->
            EntityType.Builder.<FishCurseEntity>of(FishCurseEntity::new, MobCategory.AMBIENT)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fish_curse")
                            .toString()));
    public static RegistryObject<EntityType<CyclopsCurseEntity>> CYCLOPS_CURSE = ENTITIES.register("cyclops_curse", () ->
            EntityType.Builder.<CyclopsCurseEntity>of(CyclopsCurseEntity::new, MobCategory.AMBIENT)
                    .sized(1.6F, 6.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cyclops_curse")
                            .toString()));
    public static RegistryObject<EntityType<KuchisakeOnnaEntity>> KUCHISAKE_ONNA = ENTITIES.register("kuchisake_onna", () ->
            EntityType.Builder.<KuchisakeOnnaEntity>of(KuchisakeOnnaEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "kuchisake_onna")
                            .toString()));
    public static RegistryObject<EntityType<ZombaCurseEntity>> ZOMBA_CURSE = ENTITIES.register("zomba_curse", () ->
            EntityType.Builder.<ZombaCurseEntity>of(ZombaCurseEntity::new, MobCategory.AMBIENT)
                    .sized(1.2F, 2.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "zomba_curse")
                            .toString()));
    public static RegistryObject<EntityType<WormCurseEntity>> WORM_CURSE = ENTITIES.register("worm_curse", () ->
            EntityType.Builder.<WormCurseEntity>of(WormCurseEntity::new, MobCategory.AMBIENT)
                    .sized(1.1875F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "worm_curse")
                            .toString()));
    public static RegistryObject<EntityType<FelineCurseEntity>> FELINE_CURSE = ENTITIES.register("feline_curse", () ->
            EntityType.Builder.<FelineCurseEntity>of(FelineCurseEntity::new, MobCategory.AMBIENT)
                    .sized(1.6F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "feline_curse")
                            .toString()));
    public static RegistryObject<EntityType<FuglyCurseEntity>> FUGLY_CURSE = ENTITIES.register("fugly_curse", () ->
            EntityType.Builder.<FuglyCurseEntity>of(FuglyCurseEntity::new, MobCategory.AMBIENT)
                    .sized(1.6F, 2.4F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fugly_curse")
                            .toString()));
    public static RegistryObject<EntityType<BirdCurseEntity>> BIRD_CURSE = ENTITIES.register("bird_curse", () ->
            EntityType.Builder.<BirdCurseEntity>of(BirdCurseEntity::new, MobCategory.AMBIENT)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "bird_curse")
                            .toString()));
    public static RegistryObject<EntityType<FingerBearerEntity>> FINGER_BEARER = ENTITIES.register("finger_bearer", () ->
            EntityType.Builder.<FingerBearerEntity>of(FingerBearerEntity::new, MobCategory.AMBIENT)
                    .sized(1.4F, 3.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "finger_bearer")
                            .toString()));
    public static RegistryObject<EntityType<RainbowDragonEntity>> RAINBOW_DRAGON = ENTITIES.register("rainbow_dragon", () ->
            EntityType.Builder.<RainbowDragonEntity>of(RainbowDragonEntity::new, MobCategory.AMBIENT)
                    .sized(1.5F, 1.125F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rainbow_dragon")
                            .toString()));
    public static RegistryObject<EntityType<DinoCurseEntity>> DINO_CURSE = ENTITIES.register("dino_curse", () ->
            EntityType.Builder.<DinoCurseEntity>of(DinoCurseEntity::new, MobCategory.AMBIENT)
                    .sized(4.0F, 3.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "dino_curse")
                            .toString()));
    public static RegistryObject<EntityType<AbsorbedPlayerEntity>> ABSORBED_PLAYER = ENTITIES.register("absorbed_player", () ->
            EntityType.Builder.<AbsorbedPlayerEntity>of(AbsorbedPlayerEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "absorbed_player")
                            .toString()));

    public static RegistryObject<EntityType<SukunaEntity>> SUKUNA = ENTITIES.register("sukuna", () ->
            EntityType.Builder.<SukunaEntity>of(SukunaEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "sukuna")
                            .toString()));
    public static RegistryObject<EntityType<HeianSukunaEntity>> HEIAN_SUKUNA = ENTITIES.register("heian_sukuna", () ->
            EntityType.Builder.<HeianSukunaEntity>of(HeianSukunaEntity::new, MobCategory.MISC)
                    .sized(1.0F, 2.9F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "heian_sukuna")
                            .toString()));
    public static RegistryObject<EntityType<TojiFushiguroEntity>> TOJI_FUSHIGURO = ENTITIES.register("toji_fushiguro", () ->
            EntityType.Builder.<TojiFushiguroEntity>of(TojiFushiguroEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro")
                            .toString()));
    public static RegistryObject<EntityType<SatoruGojoEntity>> SATORU_GOJO = ENTITIES.register("satoru_gojo", () ->
            EntityType.Builder.<SatoruGojoEntity>of(SatoruGojoEntity::new, MobCategory.AMBIENT)
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
    public static RegistryObject<EntityType<YujiItadoriEntity>> YUJI_ITADORI = ENTITIES.register("yuji_itadori", () ->
            EntityType.Builder.<YujiItadoriEntity>of(YujiItadoriEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuji_itadori")
                            .toString()));
    public static RegistryObject<EntityType<TogeInumakiEntity>> TOGE_INUMAKI = ENTITIES.register("toge_inumaki", () ->
            EntityType.Builder.<TogeInumakiEntity>of(TogeInumakiEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toge_inumaki")
                            .toString()));
    public static RegistryObject<EntityType<SuguruGetoEntity>> SUGURU_GETO = ENTITIES.register("suguru_geto", () ->
            EntityType.Builder.<SuguruGetoEntity>of(SuguruGetoEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "suguru_geto")
                            .toString()));
    public static RegistryObject<EntityType<NaoyaZeninEntity>> NAOYA_ZENIN = ENTITIES.register("naoya_zenin", () ->
            EntityType.Builder.<NaoyaZeninEntity>of(NaoyaZeninEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "naoya_zenin")
                            .toString()));
    public static RegistryObject<EntityType<HajimeKashimoEntity>> HAJIME_KASHIMO = ENTITIES.register("hajime_kashimo", () ->
            EntityType.Builder.<HajimeKashimoEntity>of(HajimeKashimoEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hajime_kashimo")
                            .toString()));
    public static RegistryObject<EntityType<MakiZeninEntity>> MAKI_ZENIN = ENTITIES.register("maki_zenin", () ->
            EntityType.Builder.<MakiZeninEntity>of(MakiZeninEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "maki_zenin")
                            .toString()));
    public static RegistryObject<EntityType<AoiTodoEntity>> AOI_TODO = ENTITIES.register("aoi_todo", () ->
            EntityType.Builder.<AoiTodoEntity>of(AoiTodoEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "aoi_todo")
                            .toString()));

    public static RegistryObject<EntityType<RikaEntity>> RIKA = ENTITIES.register("rika", () ->
            EntityType.Builder.<RikaEntity>of(RikaEntity::new, MobCategory.MISC)
                    .sized(1.4F, 3.875F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rika")
                            .toString()));

    public static RegistryObject<EntityType<MahoragaEntity>> MAHORAGA = ENTITIES.register("mahoraga", () ->
            EntityType.Builder.<MahoragaEntity>of(MahoragaEntity::new, MobCategory.MISC)
                    .sized(1.4F, 5.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "mahoraga").toString()));
    public static RegistryObject<EntityType<DivineDogWhiteEntity>> DIVINE_DOG_WHITE = ENTITIES.register("divine_dog_white", () ->
            EntityType.Builder.<DivineDogWhiteEntity>of(DivineDogWhiteEntity::new, MobCategory.MISC)
                    .sized(0.8F, 1.3F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_white")
                            .toString()));
    public static RegistryObject<EntityType<DivineDogBlackEntity>> DIVINE_DOG_BLACK = ENTITIES.register("divine_dog_black", () ->
            EntityType.Builder.<DivineDogBlackEntity>of(DivineDogBlackEntity::new, MobCategory.MISC)
                    .sized(0.8F, 1.3F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_black")
                            .toString()));
    public static RegistryObject<EntityType<DivineDogTotalityEntity>> DIVINE_DOG_TOTALITY = ENTITIES.register("divine_dog_totality", () ->
            EntityType.Builder.<DivineDogTotalityEntity>of(DivineDogTotalityEntity::new, MobCategory.MISC)
                    .sized(1.6F, 2.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_totality")
                            .toString()));
    public static RegistryObject<EntityType<ToadEntity>> TOAD = ENTITIES.register("toad", () ->
            EntityType.Builder.<ToadEntity>of(ToadEntity::new, MobCategory.MISC)
                    .sized(0.8F, 1.2F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad")
                            .toString()));
    public static RegistryObject<EntityType<ToadFusionEntity>> TOAD_FUSION = ENTITIES.register("toad_fusion", () ->
            EntityType.Builder.<ToadFusionEntity>of(ToadFusionEntity::new, MobCategory.MISC)
                    .sized(0.8F, 1.2F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad_fusion")
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
                    .sized(0.9F, 2.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "nue")
                            .toString()));
    public static RegistryObject<EntityType<NueTotalityEntity>> NUE_TOTALITY = ENTITIES.register("nue_totality", () ->
            EntityType.Builder.<NueTotalityEntity>of(NueTotalityEntity::new, MobCategory.MISC)
                    .sized(4.0F, 10.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "nue_totality")
                            .toString()));
    public static RegistryObject<EntityType<GreatSerpentEntity>> GREAT_SERPENT = ENTITIES.register("great_serpent", () ->
            EntityType.Builder.<GreatSerpentEntity>of(GreatSerpentEntity::new, MobCategory.MISC)
                    .sized(1.4375F, 0.8125F)
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

    public static RegistryObject<EntityType<TransfiguredSoulSmallEntity>> TRANSFIGURED_SOUL_SMALL = ENTITIES.register("transfigured_soul_small", () ->
            EntityType.Builder.<TransfiguredSoulSmallEntity>of(TransfiguredSoulSmallEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.2F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "transfigured_soul_small")
                            .toString()));
    public static RegistryObject<EntityType<TransfiguredSoulNormalEntity>> TRANSFIGURED_SOUL_NORMAL = ENTITIES.register("transfigured_soul_normal", () ->
            EntityType.Builder.<TransfiguredSoulNormalEntity>of(TransfiguredSoulNormalEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "transfigured_soul_normal")
                            .toString()));
    public static RegistryObject<EntityType<TransfiguredSoulLargeEntity>> TRANSFIGURED_SOUL_LARGE = ENTITIES.register("transfigured_soul_large", () ->
            EntityType.Builder.<TransfiguredSoulLargeEntity>of(TransfiguredSoulLargeEntity::new, MobCategory.MISC)
                    .sized(1.4F, 2.7F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "transfigured_soul_large")
                            .toString()));
    public static RegistryObject<EntityType<PolymorphicSoulIsomerEntity>> POLYMORPHIC_SOUL_ISOMER = ENTITIES.register("polymorphic_soul_isomer", () ->
            EntityType.Builder.<PolymorphicSoulIsomerEntity>of(PolymorphicSoulIsomerEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "polymorphic_soul_isomer")
                            .toString()));


    public static RegistryObject<EntityType<SimpleDomainEntity>> SIMPLE_DOMAIN = ENTITIES.register("simple_domain", () ->
            EntityType.Builder.<SimpleDomainEntity>of(SimpleDomainEntity::new, MobCategory.MISC)
                    .sized(SimpleDomainEntity.RADIUS, SimpleDomainEntity.RADIUS)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "simple_domain")
                            .toString()));
    public static RegistryObject<EntityType<RedProjectile>> RED = ENTITIES.register("red", () ->
            EntityType.Builder.<RedProjectile>of(RedProjectile::new, MobCategory.MISC)
                    .sized(0.15F, 0.15F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "red")
                            .toString()));
    public static RegistryObject<EntityType<BlueProjectile>> BLUE = ENTITIES.register("blue", () ->
            EntityType.Builder.<BlueProjectile>of(BlueProjectile::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "blue")
                            .toString()));
    public static RegistryObject<EntityType<HollowPurpleProjectile>> HOLLOW_PURPLE = ENTITIES.register("hollow_purple", () ->
            EntityType.Builder.<HollowPurpleProjectile>of(HollowPurpleProjectile::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hollow_purple")
                            .toString()));
    public static RegistryObject<EntityType<HollowPurpleExplosion>> HOLLOW_PURPLE_EXPLOSION = ENTITIES.register("hollow_purple_explosion", () ->
            EntityType.Builder.<HollowPurpleExplosion>of(HollowPurpleExplosion::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hollow_purple_explosion")
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
    public static RegistryObject<EntityType<PureLoveBeamEntity>> PURE_LOVE = ENTITIES.register("pure_love", () ->
            EntityType.Builder.<PureLoveBeamEntity>of(PureLoveBeamEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "pure_love")
                            .toString()));
    public static RegistryObject<EntityType<EmberInsectProjectile>> EMBER_INSECT = ENTITIES.register("ember_insect", () ->
            EntityType.Builder.<EmberInsectProjectile>of(EmberInsectProjectile::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "ember_insect")
                            .toString()));
    public static RegistryObject<EntityType<EmberInsectFlightEntity>> EMBER_INSECT_FLIGHT = ENTITIES.register("ember_insect_flight", () ->
            EntityType.Builder.<EmberInsectFlightEntity>of(EmberInsectFlightEntity::new, MobCategory.MISC)
                    .sized(1.0F, 0.2F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "ember_insect_flight")
                            .toString()));
    public static RegistryObject<EntityType<VolcanoEntity>> VOLCANO = ENTITIES.register("volcano", () ->
            EntityType.Builder.<VolcanoEntity>of(VolcanoEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "volcano")
                            .toString()));
    public static RegistryObject<EntityType<MeteorEntity>> METEOR = ENTITIES.register("meteor", () ->
            EntityType.Builder.<MeteorEntity>of(MeteorEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "meteor")
                            .toString()));
    public static RegistryObject<EntityType<ThrownChainProjectile>> THROWN_CHAIN = ENTITIES.register("throw_chain", () ->
            EntityType.Builder.<ThrownChainProjectile>of(ThrownChainProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "throw_chain")
                            .toString()));
    public static RegistryObject<EntityType<ScissorEntity>> SCISSOR = ENTITIES.register("scissor", () ->
            EntityType.Builder.<ScissorEntity>of(ScissorEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "scissor")
                            .toString()));
    public static RegistryObject<EntityType<FireballProjectile>> FIREBALL = ENTITIES.register("fireball", () ->
            EntityType.Builder.<FireballProjectile>of(FireballProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fireball")
                            .toString()));
    public static RegistryObject<EntityType<PiercingWaterEntity>> PIERCING_WATER = ENTITIES.register("piercing_water", () ->
            EntityType.Builder.<PiercingWaterEntity>of(PiercingWaterEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "piercing_water")
                            .toString()));
    public static RegistryObject<EntityType<JujutsuLightningEntity>> JUJUTSU_LIGHTNING = ENTITIES.register("jujutsu_lightning", () ->
            EntityType.Builder.<JujutsuLightningEntity>of(JujutsuLightningEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "jujutsu_lightning")
                            .toString()));
    public static RegistryObject<EntityType<SkyStrikeEntity>> SKY_STRIKE = ENTITIES.register("sky_strike", () ->
            EntityType.Builder.<SkyStrikeEntity>of(SkyStrikeEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "sky_strike")
                            .toString()));
    public static RegistryObject<EntityType<MaximumUzumakiProjectile>> MAXIMUM_UZUMAKI = ENTITIES.register("maximum_uzumaki", () ->
            EntityType.Builder.<MaximumUzumakiProjectile>of(MaximumUzumakiProjectile::new, MobCategory.MISC)
                    .sized(2.0F, 2.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "maximum_uzumaki")
                            .toString()));
    public static RegistryObject<EntityType<MiniUzumakiProjectile>> MINI_UZUMAKI = ENTITIES.register("mini_uzumaki", () ->
            EntityType.Builder.<MiniUzumakiProjectile>of(MiniUzumakiProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "mini_uzumaki")
                            .toString()));
    public static RegistryObject<EntityType<WaterballEntity>> WATERBALL = ENTITIES.register("waterball", () ->
            EntityType.Builder.<WaterballEntity>of(WaterballEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "waterball")
                            .toString()));
    public static RegistryObject<EntityType<EelShikigamiProjectile>> EEL_SHIKIGAMI = ENTITIES.register("eel_shikigami", () ->
            EntityType.Builder.<EelShikigamiProjectile>of(EelShikigamiProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "eel_shikigami")
                            .toString()));
    public static RegistryObject<EntityType<PiranhaShikigamiProjectile>> PIRANHA_SHIKIGAMI = ENTITIES.register("piranha_shikigami", () ->
            EntityType.Builder.<PiranhaShikigamiProjectile>of(PiranhaShikigamiProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "piranha_shikigami")
                            .toString()));
    public static RegistryObject<EntityType<SharkShikigamiProjectile>> SHARK_SHIKIGAMI = ENTITIES.register("shark_shikigami", () ->
            EntityType.Builder.<SharkShikigamiProjectile>of(SharkShikigamiProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "shark_shikigami")
                            .toString()));
    public static RegistryObject<EntityType<WaterTorrentEntity>> WATER_TORRENT = ENTITIES.register("water_torrent", () ->
            EntityType.Builder.<WaterTorrentEntity>of(WaterTorrentEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "water_torrent")
                            .toString()));
    public static RegistryObject<EntityType<ForestSpikeEntity>> FOREST_SPIKE = ENTITIES.register("forest_spike", () ->
            EntityType.Builder.<ForestSpikeEntity>of(ForestSpikeEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_spike")
                            .toString()));
    public static RegistryObject<EntityType<WoodSegmentEntity>> WOOD_SEGMENT = ENTITIES.register("wood_segment", () ->
            EntityType.Builder.<WoodSegmentEntity>of(WoodSegmentEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "wood_segment")
                            .toString()));
    public static RegistryObject<EntityType<WoodShieldSegmentEntity>> WOOD_SHIELD_SEGMENT = ENTITIES.register("wood_shield_segment", () ->
            EntityType.Builder.<WoodShieldSegmentEntity>of(WoodShieldSegmentEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "wood_shield_segment")
                            .toString()));
    public static RegistryObject<EntityType<WoodShieldEntity>> WOOD_SHIELD = ENTITIES.register("wood_shield", () ->
            EntityType.Builder.<WoodShieldEntity>of(WoodShieldEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "wood_shield")
                            .toString()));
    public static RegistryObject<EntityType<CursedBudProjectile>> CURSED_BUD = ENTITIES.register("cursed_bud", () ->
            EntityType.Builder.<CursedBudProjectile>of(CursedBudProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_bud")
                            .toString()));
    public static RegistryObject<EntityType<ForestWaveEntity>> FOREST_WAVE = ENTITIES.register("forest_wave", () ->
            EntityType.Builder.<ForestWaveEntity>of(ForestWaveEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_wave")
                            .toString()));
    public static RegistryObject<EntityType<LavaRockProjectile>> LAVA_ROCK = ENTITIES.register("lava_rock", () ->
            EntityType.Builder.<LavaRockProjectile>of(LavaRockProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "lava_rock")
                            .toString()));
    public static RegistryObject<EntityType<LightningEntity>> LIGHTNING = ENTITIES.register("lightning", () ->
            EntityType.Builder.<LightningEntity>of(LightningEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "lightning")
                            .toString()));
    public static RegistryObject<EntityType<ProjectionFrameEntity>> PROJECTION_FRAME = ENTITIES.register("projection_frame", () ->
            EntityType.Builder.<ProjectionFrameEntity>of(ProjectionFrameEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "projection_frame")
                            .toString()));
    public static RegistryObject<EntityType<AirFrameEntity>> AIR_FRAME = ENTITIES.register("air_frame", () ->
            EntityType.Builder.<AirFrameEntity>of(AirFrameEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "air_frame")
                            .toString()));
    public static RegistryObject<EntityType<BlackFlashEntity>> BLACk_FLASH = ENTITIES.register("black_flash", () ->
            EntityType.Builder.<BlackFlashEntity>of(BlackFlashEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "black_flash")
                            .toString()));
    public static RegistryObject<EntityType<ForestRootsEntity>> FOREST_ROOTS = ENTITIES.register("forest_roots", () ->
            EntityType.Builder.<ForestRootsEntity>of(ForestRootsEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_roots")
                            .toString()));
    public static RegistryObject<EntityType<FilmGaugeProjectile>> FILM_GAUGE = ENTITIES.register("film_gauge", () ->
            EntityType.Builder.<FilmGaugeProjectile>of(FilmGaugeProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "film_gauge")
                            .toString()));
    public static RegistryObject<EntityType<TimeCellMoonPalaceEntity>> TIME_CELL_MOON_PALACE = ENTITIES.register("time_cell_moon_palace", () ->
            EntityType.Builder.<TimeCellMoonPalaceEntity>of(TimeCellMoonPalaceEntity::new, MobCategory.MISC)
                    .sized(1.9F, 2.4F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "time_cell_moon_palace")
                            .toString()));
    public static RegistryObject<EntityType<SelfEmbodimentOfPerfectionEntity>> SELF_EMBODIMENT_OF_PERFECTION = ENTITIES.register("self_embodiment_of_perfection", () ->
            EntityType.Builder.<SelfEmbodimentOfPerfectionEntity>of(SelfEmbodimentOfPerfectionEntity::new, MobCategory.MISC)
                    .sized(1.4F, 2.4F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "self_embodiment_of_perfection")
                            .toString()));
    public static RegistryObject<EntityType<DisasterPlantEntity>> DISASTER_PLANT = ENTITIES.register("disaster_plant", () ->
            EntityType.Builder.<DisasterPlantEntity>of(DisasterPlantEntity::new, MobCategory.MISC)
                    .sized(3.0F, 5.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "disaster_plant")
                            .toString()));
    public static RegistryObject<EntityType<WheelEntity>> WHEEL = ENTITIES.register("wheel", () ->
            EntityType.Builder.<WheelEntity>of(WheelEntity::new, MobCategory.MISC)
                    .sized(1.0F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "wheel")
                            .toString()));
    public static RegistryObject<EntityType<NyoiStaffEntity>> NYOI_STAFF = ENTITIES.register("nyoi_staff", () ->
            EntityType.Builder.<NyoiStaffEntity>of(NyoiStaffEntity::new, MobCategory.MISC)
                    .sized(0.5F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "nyoi_staff")
                            .toString()));
    public static RegistryObject<EntityType<FireBeamEntity>> FIRE_BEAM = ENTITIES.register("fire_beam", () ->
            EntityType.Builder.<FireBeamEntity>of(FireBeamEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fire_beam")
                            .toString()));
    public static RegistryObject<EntityType<ForestDashEntity>> FOREST_DASH = ENTITIES.register("forest_dash", () ->
            EntityType.Builder.<ForestDashEntity>of(ForestDashEntity::new, MobCategory.MISC)
                    .sized(ForestDashEntity.SIZE, ForestDashEntity.SIZE)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_dash")
                            .toString()));
    public static RegistryObject<EntityType<CursedEnergyImbuedItemProjectile>> CURSED_ENERGY_IMBUED_ITEM = ENTITIES.register("cursed_energy_imbued_item", () ->
            EntityType.Builder.<CursedEnergyImbuedItemProjectile>of(CursedEnergyImbuedItemProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_energy_imbued_item")
                            .toString()));
    public static RegistryObject<EntityType<CursedEnergyBombEntity>> CURSED_ENERGY_BOMB = ENTITIES.register("cursed_energy_bomb", () ->
            EntityType.Builder.<CursedEnergyBombEntity>of(CursedEnergyBombEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_energy_bomb")
                            .toString()));
    public static RegistryObject<EntityType<CursedEnergyBlastEntity>> CURSED_ENERGY_BLAST = ENTITIES.register("cursed_energy_blast", () ->
            EntityType.Builder.<CursedEnergyBlastEntity>of(CursedEnergyBlastEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_energy_blast")
                            .toString()));
    public static RegistryObject<EntityType<EelGrappleProjectile>> EEL_GRAPPLE = ENTITIES.register("eel_grapple", () ->
            EntityType.Builder.<EelGrappleProjectile>of(EelGrappleProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "eel_grapple")
                            .toString()));
    public static RegistryObject<EntityType<TransfiguredSoulProjectile>> TRANSFIGURED_SOUL = ENTITIES.register("transfigured_soul", () ->
            EntityType.Builder.<TransfiguredSoulProjectile>of(TransfiguredSoulProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "transfigured_soul")
                            .toString()));

    @SubscribeEvent
    public static void onCreateEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(TOJI_FUSHIGURO.get(), SorcererEntity.createAttributes().build());
        event.put(SUKUNA.get(), SorcererEntity.createAttributes().build());
        event.put(SATORU_GOJO.get(), SorcererEntity.createAttributes().build());
        event.put(MEGUMI_FUSHIGURO.get(), SorcererEntity.createAttributes().build());
        event.put(YUTA_OKKOTSU.get(), SorcererEntity.createAttributes().build());
        event.put(YUJI_ITADORI.get(), SorcererEntity.createAttributes().build());
        event.put(TOGE_INUMAKI.get(), SorcererEntity.createAttributes().build());
        event.put(SUGURU_GETO.get(), SorcererEntity.createAttributes().build());
        event.put(HEIAN_SUKUNA.get(), SorcererEntity.createAttributes().build());
        event.put(NAOYA_ZENIN.get(), SorcererEntity.createAttributes().build());
        event.put(HAJIME_KASHIMO.get(), SorcererEntity.createAttributes().build());
        event.put(MAKI_ZENIN.get(), SorcererEntity.createAttributes().build());
        event.put(AOI_TODO.get(), SorcererEntity.createAttributes().build());

        event.put(RIKA.get(), RikaEntity.createAttributes().build());

        event.put(MAHORAGA.get(), MahoragaEntity.createAttributes().build());
        event.put(DIVINE_DOG_WHITE.get(), DivineDogEntity.createAttributes().build());
        event.put(DIVINE_DOG_BLACK.get(), DivineDogEntity.createAttributes().build());
        event.put(DIVINE_DOG_TOTALITY.get(), DivineDogTotalityEntity.createAttributes().build());
        event.put(TOAD.get(), ToadEntity.createAttributes().build());
        event.put(TOAD_FUSION.get(), ToadEntity.createAttributes().build());
        event.put(RABBIT_ESCAPE.get(), RabbitEscapeEntity.createAttributes().build());
        event.put(NUE.get(), NueEntity.createAttributes().build());
        event.put(NUE_TOTALITY.get(), NueTotalityEntity.createAttributes().build());
        event.put(GREAT_SERPENT.get(), GreatSerpentEntity.createAttributes().build());
        event.put(MAX_ELEPHANT.get(), MaxElephantEntity.createAttributes().build());
        event.put(TRANQUIL_DEER.get(), TranquilDeerEntity.createAttributes().build());
        event.put(PIERCING_BULL.get(), PiercingBullEntity.createAttributes().build());
        event.put(AGITO.get(), AgitoEntity.createAttributes().build());

        event.put(TRANSFIGURED_SOUL_SMALL.get(), TransfiguredSoulSmallEntity.createAttributes().build());
        event.put(TRANSFIGURED_SOUL_NORMAL.get(), SorcererEntity.createAttributes().build());
        event.put(TRANSFIGURED_SOUL_LARGE.get(), TransfiguredSoulLargeEntity.createAttributes().build());
        event.put(POLYMORPHIC_SOUL_ISOMER.get(), PolymorphicSoulIsomerEntity.createAttributes().build());

        event.put(JOGO.get(), SorcererEntity.createAttributes().build());
        event.put(JOGOAT.get(), SorcererEntity.createAttributes().build());
        event.put(DAGON.get(), SorcererEntity.createAttributes().build());
        event.put(HANAMI.get(), SorcererEntity.createAttributes().build());

        event.put(RUGBY_FIELD_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(FISH_CURSE.get(), FishCurseEntity.createAttributes().build());
        event.put(CYCLOPS_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(KUCHISAKE_ONNA.get(), KuchisakeOnnaEntity.createAttributes().build());
        event.put(ZOMBA_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(WORM_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(FELINE_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(FUGLY_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(BIRD_CURSE.get(), BirdCurseEntity.createAttributes().build());
        event.put(FINGER_BEARER.get(), SorcererEntity.createAttributes().build());
        event.put(RAINBOW_DRAGON.get(), RainbowDragonEntity.createAttributes().build());
        event.put(DINO_CURSE.get(), SorcererEntity.createAttributes().build());
        event.put(ABSORBED_PLAYER.get(), SorcererEntity.createAttributes().build());

        event.put(WOOD_SHIELD.get(), Mob.createMobAttributes().build());
    }
}
