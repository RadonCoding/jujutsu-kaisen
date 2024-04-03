package radon.jujutsu_kaisen.data;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.ability.AbilityData;
import radon.jujutsu_kaisen.data.ability.AbilityDataSerializer;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.chant.ChantData;
import radon.jujutsu_kaisen.data.chant.ChantDataSerializer;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.contract.ContractData;
import radon.jujutsu_kaisen.data.contract.ContractDataSerializer;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.curse_manipulation.CurseManipulationData;
import radon.jujutsu_kaisen.data.curse_manipulation.CurseManipulationDataSerializer;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.cursed_speech.CursedSpeechData;
import radon.jujutsu_kaisen.data.cursed_speech.CursedSpeechDataSerializer;
import radon.jujutsu_kaisen.data.cursed_speech.ICursedSpeechData;
import radon.jujutsu_kaisen.data.idle_transfiguration.IIdleTransfigurationData;
import radon.jujutsu_kaisen.data.idle_transfiguration.IdleTransfigurationData;
import radon.jujutsu_kaisen.data.idle_transfiguration.IdleTransfigurationDataSerialzer;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.mimicry.MimicryData;
import radon.jujutsu_kaisen.data.mimicry.MimicryDataSerializer;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;
import radon.jujutsu_kaisen.data.mission.entity.MissionEntityData;
import radon.jujutsu_kaisen.data.mission.entity.MissionEntityDataSerializer;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.data.mission.level.MissionLevelData;
import radon.jujutsu_kaisen.data.mission.level.MissionLevelDataSerializer;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.projection_sorcery.ProjectionSorcereryDataSerializer;
import radon.jujutsu_kaisen.data.projection_sorcery.ProjectionSorceryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererDataSerializer;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.SkillData;
import radon.jujutsu_kaisen.data.stat.SkillDataSerializer;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.data.ten_shadows.TenShadowsData;
import radon.jujutsu_kaisen.data.ten_shadows.TenShadowsDataSerializer;

import java.util.function.Supplier;

public class JJKAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, JujutsuKaisen.MOD_ID);

    public static final Supplier<AttachmentType<ISorcererData>> SORCERER = ATTACHMENT_TYPES.register("sorcerer",
            AttachmentType.<ISorcererData>builder(holder -> new SorcererData((LivingEntity) holder)).serialize(new SorcererDataSerializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<IAbilityData>> ABILITY = ATTACHMENT_TYPES.register("ability",
            AttachmentType.<IAbilityData>builder(holder -> new AbilityData((LivingEntity) holder)).serialize(new AbilityDataSerializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<IChantData>> CHANT = ATTACHMENT_TYPES.register("chant",
            AttachmentType.<IChantData>builder(holder -> new ChantData()).serialize(new ChantDataSerializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<IContractData>> CONTRACT = ATTACHMENT_TYPES.register("contract",
            AttachmentType.<IContractData>builder(holder -> new ContractData()).serialize(new ContractDataSerializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<ITenShadowsData>> TEN_SHADOWS = ATTACHMENT_TYPES.register("ten_shadows",
            AttachmentType.<ITenShadowsData>builder(holder -> new TenShadowsData((LivingEntity) holder)).serialize(new TenShadowsDataSerializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<ICurseManipulationData>> CURSE_MANIPULATION = ATTACHMENT_TYPES.register("curse_manipulation",
            AttachmentType.<ICurseManipulationData>builder(holder -> new CurseManipulationData((LivingEntity) holder)).serialize(new CurseManipulationDataSerializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<IProjectionSorceryData>> PROJECTION_SORCERY = ATTACHMENT_TYPES.register("projection_sorcery",
            AttachmentType.<IProjectionSorceryData>builder(holder -> new ProjectionSorceryData((LivingEntity) holder)).serialize(new ProjectionSorcereryDataSerializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<IIdleTransfigurationData>> IDLE_TRANSFIGURATION = ATTACHMENT_TYPES.register("idle_transfiguration",
            AttachmentType.<IIdleTransfigurationData>builder(holder -> new IdleTransfigurationData()).serialize(new IdleTransfigurationDataSerialzer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<IMimicryData>> MIMICRY = ATTACHMENT_TYPES.register("mimicry",
            AttachmentType.<IMimicryData>builder(holder -> new MimicryData((LivingEntity) holder)).serialize(new MimicryDataSerializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<ICursedSpeechData>> CURSED_SPEECH = ATTACHMENT_TYPES.register("cursed_speech",
            AttachmentType.<ICursedSpeechData>builder(holder -> new CursedSpeechData()).serialize(new CursedSpeechDataSerializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<ISkillData>> SKILL = ATTACHMENT_TYPES.register("skill",
            AttachmentType.<ISkillData>builder(holder -> new SkillData()).serialize(new SkillDataSerializer()).copyOnDeath()::build);

    public static final Supplier<AttachmentType<IMissionLevelData>> MISSION_LEVEL = ATTACHMENT_TYPES.register("mission_level",
            AttachmentType.<IMissionLevelData>builder(holder -> new MissionLevelData((Level) holder)).serialize(new MissionLevelDataSerializer())::build);
    public static final Supplier<AttachmentType<IMissionEntityData>> MISSION_ENTITY = ATTACHMENT_TYPES.register("mission_entity",
            AttachmentType.<IMissionEntityData>builder(holder -> new MissionEntityData((LivingEntity) holder)).serialize(new MissionEntityDataSerializer()).copyOnDeath()::build);
}
