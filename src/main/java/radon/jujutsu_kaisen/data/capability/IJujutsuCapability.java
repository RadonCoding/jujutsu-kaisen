package radon.jujutsu_kaisen.data.capability;

import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.cursed_speech.ICursedSpeechData;
import radon.jujutsu_kaisen.data.idle_transfiguration.IIdleTransfigurationData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;

public interface IJujutsuCapability {
    ISorcererData getSorcererData();

    IAbilityData getAbilityData();

    IChantData getChantData();

    IContractData getContractData();

    ITenShadowsData getTenShadowsData();

    ICurseManipulationData getCurseManipulationData();

    IProjectionSorceryData getProjectionSorceryData();

    IIdleTransfigurationData getIdleTransfigurationData();

    IMimicryData getMimicryData();

    ICursedSpeechData getCursedSpeechData();

    ISkillData getSkillData();

    IMissionEntityData getMissionData();
}
