package radon.jujutsu_kaisen.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.domain.ChimeraShadowGardenEntity;
import radon.jujutsu_kaisen.entity.domain.SelfEmbodimentOfPerfectionEntity;
import radon.jujutsu_kaisen.entity.domain.AuthenticMutualLoveEntity;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.MalevolentShrineEntity;
import radon.jujutsu_kaisen.entity.domain.TimeCellMoonPalaceEntity;
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
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<EntityType<?>, EntityType<ClosedDomainExpansionEntity>> CLOSED_DOMAIN_EXPANSION = ENTITIES.register("closed_domain_expansion", () ->
            EntityType.Builder.<ClosedDomainExpansionEntity>of(ClosedDomainExpansionEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "closed_domain_expansion")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<AuthenticMutualLoveEntity>> GENUINE_MUTUAL_LOVE = ENTITIES.register("genuine_mutual_love", () ->
            EntityType.Builder.<AuthenticMutualLoveEntity>of(AuthenticMutualLoveEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "genuine_mutual_love")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<MalevolentShrineEntity>> MALEVOLENT_SHRINE = ENTITIES.register("malevolent_shrine", () ->
            EntityType.Builder.<MalevolentShrineEntity>of(MalevolentShrineEntity::new, MobCategory.MISC)
                    .sized(8.4F, 9.6F)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "malevolent_shrine")
                            .toString()));

    public static DeferredHolder<EntityType<?>, EntityType<ChimeraShadowGardenEntity>> CHIMERA_SHADOW_GARDEN = ENTITIES.register("chimera_shadow_garden", () ->
            EntityType.Builder.<ChimeraShadowGardenEntity>of(ChimeraShadowGardenEntity::new, MobCategory.MISC)
                    .sized(3.0F, 4.0F)
                    .fireImmune()
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "chimera_shadow_garden")
                            .toString()));

    public static DeferredHolder<EntityType<?>, EntityType<VeilEntity>> VEIL = ENTITIES.register("veil", () ->
            EntityType.Builder.<VeilEntity>of(VeilEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "veil")
                            .toString()));

    public static DeferredHolder<EntityType<?>, EntityType<JogoEntity>> JOGO = ENTITIES.register("jogo", () ->
            EntityType.Builder.<JogoEntity>of(JogoEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 1.9F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "jogo")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<JogoatEntity>> JOGOAT = ENTITIES.register("jogoat", () ->
            EntityType.Builder.<JogoatEntity>of(JogoatEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 2.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "jogoat")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<DagonEntity>> DAGON = ENTITIES.register("dagon", () ->
            EntityType.Builder.<DagonEntity>of(DagonEntity::new, MobCategory.CREATURE)
                    .sized(1.4F, 3.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "dagon")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<HanamiEntity>> HANAMI = ENTITIES.register("hanami", () ->
            EntityType.Builder.<HanamiEntity>of(HanamiEntity::new, MobCategory.CREATURE)
                    .sized(1.4F, 3.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hanami")
                            .toString()));

    public static DeferredHolder<EntityType<?>, EntityType<RugbyFieldCurseEntity>> RUGBY_FIELD_CURSE = ENTITIES.register("rugby_field_curse", () ->
            EntityType.Builder.of(RugbyFieldCurseEntity::new, MobCategory.CREATURE)
                    .sized(2.0F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rugby_field_curse")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<FishCurseEntity>> FISH_CURSE = ENTITIES.register("fish_curse", () ->
            EntityType.Builder.<FishCurseEntity>of(FishCurseEntity::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fish_curse")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<CyclopsCurseEntity>> CYCLOPS_CURSE = ENTITIES.register("cyclops_curse", () ->
            EntityType.Builder.<CyclopsCurseEntity>of(CyclopsCurseEntity::new, MobCategory.CREATURE)
                    .sized(1.6F, 6.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cyclops_curse")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<KuchisakeOnnaEntity>> KUCHISAKE_ONNA = ENTITIES.register("kuchisake_onna", () ->
            EntityType.Builder.<KuchisakeOnnaEntity>of(KuchisakeOnnaEntity::new, MobCategory.CREATURE)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "kuchisake_onna")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ZombaCurseEntity>> ZOMBA_CURSE = ENTITIES.register("zomba_curse", () ->
            EntityType.Builder.<ZombaCurseEntity>of(ZombaCurseEntity::new, MobCategory.CREATURE)
                    .sized(1.2F, 2.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "zomba_curse")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<WormCurseEntity>> WORM_CURSE = ENTITIES.register("worm_curse", () ->
            EntityType.Builder.<WormCurseEntity>of(WormCurseEntity::new, MobCategory.CREATURE)
                    .sized(1.1875F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "worm_curse")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<FelineCurseEntity>> FELINE_CURSE = ENTITIES.register("feline_curse", () ->
            EntityType.Builder.<FelineCurseEntity>of(FelineCurseEntity::new, MobCategory.CREATURE)
                    .sized(1.6F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "feline_curse")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<FuglyCurseEntity>> FUGLY_CURSE = ENTITIES.register("fugly_curse", () ->
            EntityType.Builder.<FuglyCurseEntity>of(FuglyCurseEntity::new, MobCategory.CREATURE)
                    .sized(1.6F, 2.4F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fugly_curse")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<BirdCurseEntity>> BIRD_CURSE = ENTITIES.register("bird_curse", () ->
            EntityType.Builder.<BirdCurseEntity>of(BirdCurseEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "bird_curse")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<FingerBearerEntity>> FINGER_BEARER = ENTITIES.register("finger_bearer", () ->
            EntityType.Builder.<FingerBearerEntity>of(FingerBearerEntity::new, MobCategory.CREATURE)
                    .sized(1.4F, 3.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "finger_bearer")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<RainbowDragonEntity>> RAINBOW_DRAGON = ENTITIES.register("rainbow_dragon", () ->
            EntityType.Builder.<RainbowDragonEntity>of(RainbowDragonEntity::new, MobCategory.CREATURE)
                    .sized(1.5F, 1.125F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rainbow_dragon")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<DinoCurseEntity>> DINO_CURSE = ENTITIES.register("dino_curse", () ->
            EntityType.Builder.<DinoCurseEntity>of(DinoCurseEntity::new, MobCategory.CREATURE)
                    .sized(4.0F, 3.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "dino_curse")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<KoGuyEntity>> KO_GUY = ENTITIES.register("ko_guy", () ->
            EntityType.Builder.<KoGuyEntity>of(KoGuyEntity::new, MobCategory.CREATURE)
                    .sized(2.0F, 2.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "ko_guy")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<AbsorbedPlayerEntity>> ABSORBED_PLAYER = ENTITIES.register("absorbed_player", () ->
            EntityType.Builder.<AbsorbedPlayerEntity>of(AbsorbedPlayerEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "absorbed_player")
                            .toString()));

    public static DeferredHolder<EntityType<?>, EntityType<SukunaEntity>> SUKUNA = ENTITIES.register("sukuna", () ->
            EntityType.Builder.<SukunaEntity>of(SukunaEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "sukuna")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<HeianSukunaEntity>> HEIAN_SUKUNA = ENTITIES.register("heian_sukuna", () ->
            EntityType.Builder.<HeianSukunaEntity>of(HeianSukunaEntity::new, MobCategory.MISC)
                    .sized(1.0F, 2.9F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "heian_sukuna")
                            .toString()));

    public static DeferredHolder<EntityType<?>, EntityType<TojiFushiguroEntity>> TOJI_FUSHIGURO = ENTITIES.register("toji_fushiguro", () ->
            EntityType.Builder.<TojiFushiguroEntity>of(TojiFushiguroEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<SatoruGojoEntity>> SATORU_GOJO = ENTITIES.register("satoru_gojo", () ->
            EntityType.Builder.<SatoruGojoEntity>of(SatoruGojoEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "satoru_gojo")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<YutaOkkotsuEntity>> YUTA_OKKOTSU = ENTITIES.register("yuta_okkotsu", () ->
            EntityType.Builder.<YutaOkkotsuEntity>of(YutaOkkotsuEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuta_okkotsu")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<MegumiFushiguroEntity>> MEGUMI_FUSHIGURO = ENTITIES.register("megumi_fushiguro", () ->
            EntityType.Builder.<MegumiFushiguroEntity>of(MegumiFushiguroEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "megumi_fushiguro")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<YujiItadoriEntity>> YUJI_ITADORI = ENTITIES.register("yuji_itadori", () ->
            EntityType.Builder.<YujiItadoriEntity>of(YujiItadoriEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuji_itadori")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<TogeInumakiEntity>> TOGE_INUMAKI = ENTITIES.register("toge_inumaki", () ->
            EntityType.Builder.<TogeInumakiEntity>of(TogeInumakiEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toge_inumaki")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<SuguruGetoEntity>> SUGURU_GETO = ENTITIES.register("suguru_geto", () ->
            EntityType.Builder.<SuguruGetoEntity>of(SuguruGetoEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "suguru_geto")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<NaoyaZeninEntity>> NAOYA_ZENIN = ENTITIES.register("naoya_zenin", () ->
            EntityType.Builder.<NaoyaZeninEntity>of(NaoyaZeninEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "naoya_zenin")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<HajimeKashimoEntity>> HAJIME_KASHIMO = ENTITIES.register("hajime_kashimo", () ->
            EntityType.Builder.<HajimeKashimoEntity>of(HajimeKashimoEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hajime_kashimo")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<MakiZeninEntity>> MAKI_ZENIN = ENTITIES.register("maki_zenin", () ->
            EntityType.Builder.<MakiZeninEntity>of(MakiZeninEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "maki_zenin")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<AoiTodoEntity>> AOI_TODO = ENTITIES.register("aoi_todo", () ->
            EntityType.Builder.<AoiTodoEntity>of(AoiTodoEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "aoi_todo")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<MiwaKasumiEntity>> MIWA_KASUMI = ENTITIES.register("miwa_kasumi", () ->
            EntityType.Builder.<MiwaKasumiEntity>of(MiwaKasumiEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "miwa_kasumi")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<WindowEntity>> WINDOW = ENTITIES.register("window", () ->
            EntityType.Builder.<WindowEntity>of(WindowEntity::new, MobCategory.AMBIENT)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "window")
                            .toString()));


    public static DeferredHolder<EntityType<?>, EntityType<RikaEntity>> RIKA = ENTITIES.register("rika", () ->
            EntityType.Builder.<RikaEntity>of(RikaEntity::new, MobCategory.MISC)
                    .sized(1.4F, 3.875F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rika")
                            .toString()));

    public static DeferredHolder<EntityType<?>, EntityType<MahoragaEntity>> MAHORAGA = ENTITIES.register("mahoraga", () ->
            EntityType.Builder.<MahoragaEntity>of(MahoragaEntity::new, MobCategory.MISC)
                    .sized(1.4F, 5.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "mahoraga").toString()));
    public static DeferredHolder<EntityType<?>, EntityType<DivineDogWhiteEntity>> DIVINE_DOG_WHITE = ENTITIES.register("divine_dog_white", () ->
            EntityType.Builder.<DivineDogWhiteEntity>of(DivineDogWhiteEntity::new, MobCategory.MISC)
                    .sized(0.8F, 1.3F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_white")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<DivineDogBlackEntity>> DIVINE_DOG_BLACK = ENTITIES.register("divine_dog_black", () ->
            EntityType.Builder.<DivineDogBlackEntity>of(DivineDogBlackEntity::new, MobCategory.MISC)
                    .sized(0.8F, 1.3F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_black")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<DivineDogTotalityEntity>> DIVINE_DOG_TOTALITY = ENTITIES.register("divine_dog_totality", () ->
            EntityType.Builder.<DivineDogTotalityEntity>of(DivineDogTotalityEntity::new, MobCategory.MISC)
                    .sized(1.6F, 2.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog_totality")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ToadEntity>> TOAD = ENTITIES.register("toad", () ->
            EntityType.Builder.<ToadEntity>of(ToadEntity::new, MobCategory.MISC)
                    .sized(0.8F, 1.2F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ToadFusionEntity>> TOAD_FUSION = ENTITIES.register("toad_fusion", () ->
            EntityType.Builder.<ToadFusionEntity>of(ToadFusionEntity::new, MobCategory.MISC)
                    .sized(0.8F, 1.2F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad_fusion")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ToadTongueProjectile>> TOAD_TONGUE = ENTITIES.register("toad_tongue", () ->
            EntityType.Builder.<ToadTongueProjectile>of(ToadTongueProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "toad_tongue")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<RabbitEscapeEntity>> RABBIT_ESCAPE = ENTITIES.register("rabbit_escape", () ->
            EntityType.Builder.<RabbitEscapeEntity>of(RabbitEscapeEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "rabbit_escape")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<NueEntity>> NUE = ENTITIES.register("nue", () ->
            EntityType.Builder.<NueEntity>of(NueEntity::new, MobCategory.MISC)
                    .sized(0.9F, 2.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "nue")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<NueTotalityEntity>> NUE_TOTALITY = ENTITIES.register("nue_totality", () ->
            EntityType.Builder.<NueTotalityEntity>of(NueTotalityEntity::new, MobCategory.MISC)
                    .sized(4.0F, 10.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "nue_totality")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<GreatSerpentEntity>> GREAT_SERPENT = ENTITIES.register("great_serpent", () ->
            EntityType.Builder.<GreatSerpentEntity>of(GreatSerpentEntity::new, MobCategory.MISC)
                    .sized(1.4375F, 0.8125F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "great_serpent")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<MaxElephantEntity>> MAX_ELEPHANT = ENTITIES.register("max_elephant", () ->
            EntityType.Builder.<MaxElephantEntity>of(MaxElephantEntity::new, MobCategory.MISC)
                    .sized(3.8F, 3.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "max_elephant")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<TranquilDeerEntity>> TRANQUIL_DEER = ENTITIES.register("tranquil_deer", () ->
            EntityType.Builder.<TranquilDeerEntity>of(TranquilDeerEntity::new, MobCategory.MISC)
                    .sized(3.0F, 3.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "tranquil_deer")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<PiercingBullEntity>> PIERCING_BULL = ENTITIES.register("piercing_bull", () ->
            EntityType.Builder.<PiercingBullEntity>of(PiercingBullEntity::new, MobCategory.MISC)
                    .sized(2.0F, 1.8F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "piercing_bull")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<AgitoEntity>> AGITO = ENTITIES.register("agito", () ->
            EntityType.Builder.<AgitoEntity>of(AgitoEntity::new, MobCategory.MISC)
                    .sized(1.6F, 4.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "agito")
                            .toString()));

    public static DeferredHolder<EntityType<?>, EntityType<TransfiguredSoulSmallEntity>> TRANSFIGURED_SOUL_SMALL = ENTITIES.register("transfigured_soul_small", () ->
            EntityType.Builder.<TransfiguredSoulSmallEntity>of(TransfiguredSoulSmallEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.2F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "transfigured_soul_small")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<TransfiguredSoulNormalEntity>> TRANSFIGURED_SOUL_NORMAL = ENTITIES.register("transfigured_soul_normal", () ->
            EntityType.Builder.<TransfiguredSoulNormalEntity>of(TransfiguredSoulNormalEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "transfigured_soul_normal")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<TransfiguredSoulLargeEntity>> TRANSFIGURED_SOUL_LARGE = ENTITIES.register("transfigured_soul_large", () ->
            EntityType.Builder.<TransfiguredSoulLargeEntity>of(TransfiguredSoulLargeEntity::new, MobCategory.MISC)
                    .sized(1.4F, 2.7F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "transfigured_soul_large")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<PolymorphicSoulIsomerEntity>> POLYMORPHIC_SOUL_ISOMER = ENTITIES.register("polymorphic_soul_isomer", () ->
            EntityType.Builder.<PolymorphicSoulIsomerEntity>of(PolymorphicSoulIsomerEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "polymorphic_soul_isomer")
                            .toString()));


    public static DeferredHolder<EntityType<?>, EntityType<SimpleDomainEntity>> SIMPLE_DOMAIN = ENTITIES.register("simple_domain", () ->
            EntityType.Builder.<SimpleDomainEntity>of(SimpleDomainEntity::new, MobCategory.MISC)
                    .sized(SimpleDomainEntity.RADIUS, SimpleDomainEntity.RADIUS)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "simple_domain")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<RedProjectile>> RED = ENTITIES.register("red", () ->
            EntityType.Builder.<RedProjectile>of(RedProjectile::new, MobCategory.MISC)
                    .sized(0.15F, 0.15F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "red")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<BlueProjectile>> BLUE = ENTITIES.register("blue", () ->
            EntityType.Builder.<BlueProjectile>of(BlueProjectile::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "blue")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<HollowPurpleProjectile>> HOLLOW_PURPLE = ENTITIES.register("hollow_purple", () ->
            EntityType.Builder.<HollowPurpleProjectile>of(HollowPurpleProjectile::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hollow_purple")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<HollowPurpleExplosion>> HOLLOW_PURPLE_EXPLOSION = ENTITIES.register("hollow_purple_explosion", () ->
            EntityType.Builder.<HollowPurpleExplosion>of(HollowPurpleExplosion::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "hollow_purple_explosion")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<DismantleProjectile>> DISMANTLE = ENTITIES.register("dismantle", () ->
            EntityType.Builder.<DismantleProjectile>of(DismantleProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "dismantle")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<BigDismantleProjectile>> BIG_DISMANTLE = ENTITIES.register("big_dismantle", () ->
            EntityType.Builder.<BigDismantleProjectile>of(BigDismantleProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "big_dismantle")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<WorldSlashProjectile>> WORLD_SLASH = ENTITIES.register("world_slash", () ->
            EntityType.Builder.<WorldSlashProjectile>of(WorldSlashProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "world_slash")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<FireArrowProjectile>> FIRE_ARROW = ENTITIES.register("fire_arrow", () ->
            EntityType.Builder.<FireArrowProjectile>of(FireArrowProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fire_arrow")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<PureLoveBeamEntity>> PURE_LOVE = ENTITIES.register("pure_love", () ->
            EntityType.Builder.<PureLoveBeamEntity>of(PureLoveBeamEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "pure_love")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<EmberInsectProjectile>> EMBER_INSECT = ENTITIES.register("ember_insect", () ->
            EntityType.Builder.<EmberInsectProjectile>of(EmberInsectProjectile::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "ember_insect")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<EmberInsectFlightEntity>> EMBER_INSECT_FLIGHT = ENTITIES.register("ember_insect_flight", () ->
            EntityType.Builder.<EmberInsectFlightEntity>of(EmberInsectFlightEntity::new, MobCategory.MISC)
                    .sized(1.0F, 0.2F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "ember_insect_flight")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<VolcanoEntity>> VOLCANO = ENTITIES.register("volcano", () ->
            EntityType.Builder.<VolcanoEntity>of(VolcanoEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "volcano")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<MeteorEntity>> METEOR = ENTITIES.register("meteor", () ->
            EntityType.Builder.<MeteorEntity>of(MeteorEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "meteor")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ThrownChainProjectile>> THROWN_CHAIN = ENTITIES.register("throw_chain", () ->
            EntityType.Builder.<ThrownChainProjectile>of(ThrownChainProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "throw_chain")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ScissorEntity>> SCISSOR = ENTITIES.register("scissor", () ->
            EntityType.Builder.<ScissorEntity>of(ScissorEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "scissor")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<FireballProjectile>> FIREBALL = ENTITIES.register("fireball", () ->
            EntityType.Builder.<FireballProjectile>of(FireballProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fireball")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<PiercingWaterEntity>> PIERCING_WATER = ENTITIES.register("piercing_water", () ->
            EntityType.Builder.<PiercingWaterEntity>of(PiercingWaterEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "piercing_water")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<JujutsuLightningEntity>> JUJUTSU_LIGHTNING = ENTITIES.register("jujutsu_lightning", () ->
            EntityType.Builder.<JujutsuLightningEntity>of(JujutsuLightningEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "jujutsu_lightning")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<SkyStrikeEntity>> SKY_STRIKE = ENTITIES.register("sky_strike", () ->
            EntityType.Builder.<SkyStrikeEntity>of(SkyStrikeEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "sky_strike")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<MaximumUzumakiProjectile>> MAXIMUM_UZUMAKI = ENTITIES.register("maximum_uzumaki", () ->
            EntityType.Builder.<MaximumUzumakiProjectile>of(MaximumUzumakiProjectile::new, MobCategory.MISC)
                    .sized(2.0F, 2.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "maximum_uzumaki")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<MiniUzumakiProjectile>> MINI_UZUMAKI = ENTITIES.register("mini_uzumaki", () ->
            EntityType.Builder.<MiniUzumakiProjectile>of(MiniUzumakiProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "mini_uzumaki")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<WaterballEntity>> WATERBALL = ENTITIES.register("waterball", () ->
            EntityType.Builder.<WaterballEntity>of(WaterballEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "waterball")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<EelShikigamiProjectile>> EEL_SHIKIGAMI = ENTITIES.register("eel_shikigami", () ->
            EntityType.Builder.<EelShikigamiProjectile>of(EelShikigamiProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "eel_shikigami")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<PiranhaShikigamiProjectile>> PIRANHA_SHIKIGAMI = ENTITIES.register("piranha_shikigami", () ->
            EntityType.Builder.<PiranhaShikigamiProjectile>of(PiranhaShikigamiProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "piranha_shikigami")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<SharkShikigamiProjectile>> SHARK_SHIKIGAMI = ENTITIES.register("shark_shikigami", () ->
            EntityType.Builder.<SharkShikigamiProjectile>of(SharkShikigamiProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "shark_shikigami")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<WaterTorrentEntity>> WATER_TORRENT = ENTITIES.register("water_torrent", () ->
            EntityType.Builder.<WaterTorrentEntity>of(WaterTorrentEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "water_torrent")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ForestSpikeEntity>> FOREST_SPIKE = ENTITIES.register("forest_spike", () ->
            EntityType.Builder.<ForestSpikeEntity>of(ForestSpikeEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_spike")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<WoodSegmentEntity>> WOOD_SEGMENT = ENTITIES.register("wood_segment", () ->
            EntityType.Builder.<WoodSegmentEntity>of(WoodSegmentEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "wood_segment")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<WoodShieldSegmentEntity>> WOOD_SHIELD_SEGMENT = ENTITIES.register("wood_shield_segment", () ->
            EntityType.Builder.<WoodShieldSegmentEntity>of(WoodShieldSegmentEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "wood_shield_segment")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<WoodShieldEntity>> WOOD_SHIELD = ENTITIES.register("wood_shield", () ->
            EntityType.Builder.<WoodShieldEntity>of(WoodShieldEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "wood_shield")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<CursedBudProjectile>> CURSED_BUD = ENTITIES.register("cursed_bud", () ->
            EntityType.Builder.<CursedBudProjectile>of(CursedBudProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_bud")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ForestWaveEntity>> FOREST_WAVE = ENTITIES.register("forest_wave", () ->
            EntityType.Builder.<ForestWaveEntity>of(ForestWaveEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_wave")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<LavaRockProjectile>> LAVA_ROCK = ENTITIES.register("lava_rock", () ->
            EntityType.Builder.<LavaRockProjectile>of(LavaRockProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "lava_rock")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<LightningEntity>> LIGHTNING = ENTITIES.register("lightning", () ->
            EntityType.Builder.<LightningEntity>of(LightningEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "lightning")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ProjectionFrameEntity>> PROJECTION_FRAME = ENTITIES.register("projection_frame", () ->
            EntityType.Builder.<ProjectionFrameEntity>of(ProjectionFrameEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "projection_frame")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<AirFrameEntity>> AIR_FRAME = ENTITIES.register("air_frame", () ->
            EntityType.Builder.<AirFrameEntity>of(AirFrameEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "air_frame")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<BlackFlashEntity>> BLACk_FLASH = ENTITIES.register("black_flash", () ->
            EntityType.Builder.<BlackFlashEntity>of(BlackFlashEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "black_flash")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ForestRootsEntity>> FOREST_ROOTS = ENTITIES.register("forest_roots", () ->
            EntityType.Builder.<ForestRootsEntity>of(ForestRootsEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_roots")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<FilmGaugeProjectile>> FILM_GAUGE = ENTITIES.register("film_gauge", () ->
            EntityType.Builder.<FilmGaugeProjectile>of(FilmGaugeProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "film_gauge")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<TimeCellMoonPalaceEntity>> TIME_CELL_MOON_PALACE = ENTITIES.register("time_cell_moon_palace", () ->
            EntityType.Builder.<TimeCellMoonPalaceEntity>of(TimeCellMoonPalaceEntity::new, MobCategory.MISC)
                    .sized(1.9F, 2.4F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "time_cell_moon_palace")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<SelfEmbodimentOfPerfectionEntity>> SELF_EMBODIMENT_OF_PERFECTION = ENTITIES.register("self_embodiment_of_perfection", () ->
            EntityType.Builder.<SelfEmbodimentOfPerfectionEntity>of(SelfEmbodimentOfPerfectionEntity::new, MobCategory.MISC)
                    .sized(3.0F, 3.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "self_embodiment_of_perfection")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<DisasterPlantEntity>> DISASTER_PLANT = ENTITIES.register("disaster_plant", () ->
            EntityType.Builder.<DisasterPlantEntity>of(DisasterPlantEntity::new, MobCategory.MISC)
                    .sized(3.0F, 5.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "disaster_plant")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<WheelEntity>> WHEEL = ENTITIES.register("wheel", () ->
            EntityType.Builder.<WheelEntity>of(WheelEntity::new, MobCategory.MISC)
                    .sized(1.0F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "wheel")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<NyoiStaffEntity>> NYOI_STAFF = ENTITIES.register("nyoi_staff", () ->
            EntityType.Builder.<NyoiStaffEntity>of(NyoiStaffEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "nyoi_staff")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<MimicryKatanaEntity>> MIMICRY_KATANA = ENTITIES.register("mimicry_katana", () ->
            EntityType.Builder.<MimicryKatanaEntity>of(MimicryKatanaEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "mimicry_katana")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<FireBeamEntity>> FIRE_BEAM = ENTITIES.register("fire_beam", () ->
            EntityType.Builder.<FireBeamEntity>of(FireBeamEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "fire_beam")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ForestDashEntity>> FOREST_DASH = ENTITIES.register("forest_dash", () ->
            EntityType.Builder.<ForestDashEntity>of(ForestDashEntity::new, MobCategory.MISC)
                    .sized(ForestDashEntity.SIZE, ForestDashEntity.SIZE)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_dash")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<CursedEnergyImbuedItemProjectile>> CURSED_ENERGY_IMBUED_ITEM = ENTITIES.register("cursed_energy_imbued_item", () ->
            EntityType.Builder.<CursedEnergyImbuedItemProjectile>of(CursedEnergyImbuedItemProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_energy_imbued_item")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<CursedEnergyBombEntity>> CURSED_ENERGY_BOMB = ENTITIES.register("cursed_energy_bomb", () ->
            EntityType.Builder.<CursedEnergyBombEntity>of(CursedEnergyBombEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_energy_bomb")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<CursedEnergyBlastEntity>> CURSED_ENERGY_BLAST = ENTITIES.register("cursed_energy_blast", () ->
            EntityType.Builder.<CursedEnergyBlastEntity>of(CursedEnergyBlastEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_energy_blast")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<EelGrappleProjectile>> EEL_GRAPPLE = ENTITIES.register("eel_grapple", () ->
            EntityType.Builder.<EelGrappleProjectile>of(EelGrappleProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "eel_grapple")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<TransfiguredSoulProjectile>> TRANSFIGURED_SOUL = ENTITIES.register("transfigured_soul", () ->
            EntityType.Builder.<TransfiguredSoulProjectile>of(TransfiguredSoulProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "transfigured_soul")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<ElectricBlastEntity>> ELECTRIC_BLAST = ENTITIES.register("electric_blast", () ->
            EntityType.Builder.<ElectricBlastEntity>of(ElectricBlastEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "electric_blast")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<BodyRepelEntity>> BODY_REPEL = ENTITIES.register("body_repel", () ->
            EntityType.Builder.<BodyRepelEntity>of(BodyRepelEntity::new, MobCategory.MISC)
                    .sized(1.3125F, 1.375F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "body_repel")
                            .toString()));
    public static DeferredHolder<EntityType<?>, EntityType<FerociousBodyRepelEntity>> FEROCIOUS_BODY_REPEL = ENTITIES.register("ferocious_body_repel", () ->
            EntityType.Builder.<FerociousBodyRepelEntity>of(FerociousBodyRepelEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.6F)
                    .build(new ResourceLocation(JujutsuKaisen.MOD_ID, "ferocious_body_repel")
                            .toString()));

    @SubscribeEvent
    public static void onSpawnPlacementsRegister(SpawnPlacementRegisterEvent event) {
        SpawnPlacements.register(TOJI_FUSHIGURO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(SATORU_GOJO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(MEGUMI_FUSHIGURO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(YUTA_OKKOTSU.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(YUJI_ITADORI.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(TOGE_INUMAKI.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(SUGURU_GETO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(NAOYA_ZENIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(HAJIME_KASHIMO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(MAKI_ZENIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(AOI_TODO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(MIWA_KASUMI.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(WINDOW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);

        SpawnPlacements.register(RUGBY_FIELD_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(FISH_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(CYCLOPS_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(KUCHISAKE_ONNA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(ZOMBA_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(WORM_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(FELINE_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(FUGLY_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(BIRD_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(FINGER_BEARER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(RAINBOW_DRAGON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(DINO_CURSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(KO_GUY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
    }

    @SubscribeEvent
    public static void onCreateEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(SUKUNA.get(), SorcererEntity.createAttributes().build());
        event.put(HEIAN_SUKUNA.get(), SorcererEntity.createAttributes().build());

        event.put(TOJI_FUSHIGURO.get(), SorcererEntity.createAttributes().build());
        event.put(SATORU_GOJO.get(), SorcererEntity.createAttributes().build());
        event.put(MEGUMI_FUSHIGURO.get(), SorcererEntity.createAttributes().build());
        event.put(YUTA_OKKOTSU.get(), SorcererEntity.createAttributes().build());
        event.put(YUJI_ITADORI.get(), SorcererEntity.createAttributes().build());
        event.put(TOGE_INUMAKI.get(), SorcererEntity.createAttributes().build());
        event.put(SUGURU_GETO.get(), SorcererEntity.createAttributes().build());
        event.put(NAOYA_ZENIN.get(), SorcererEntity.createAttributes().build());
        event.put(HAJIME_KASHIMO.get(), SorcererEntity.createAttributes().build());
        event.put(MAKI_ZENIN.get(), SorcererEntity.createAttributes().build());
        event.put(AOI_TODO.get(), SorcererEntity.createAttributes().build());
        event.put(MIWA_KASUMI.get(), SorcererEntity.createAttributes().build());
        event.put(WINDOW.get(), SorcererEntity.createAttributes().build());

        event.put(RIKA.get(), RikaEntity.createAttributes().build());

        event.put(MAHORAGA.get(), MahoragaEntity.createAttributes().build());
        event.put(DIVINE_DOG_WHITE.get(), DivineDogEntity.createAttributes().build());
        event.put(DIVINE_DOG_BLACK.get(), DivineDogEntity.createAttributes().build());
        event.put(DIVINE_DOG_TOTALITY.get(), DivineDogTotalityEntity.createAttributes().build());
        event.put(TOAD.get(), ToadEntity.createAttributes().build());
        event.put(TOAD_FUSION.get(), ToadFusionEntity.createAttributes().build());
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
        event.put(KO_GUY.get(), SorcererEntity.createAttributes().build());
        event.put(ABSORBED_PLAYER.get(), SorcererEntity.createAttributes().build());

        event.put(WOOD_SHIELD.get(), Mob.createMobAttributes().build());
    }
}
