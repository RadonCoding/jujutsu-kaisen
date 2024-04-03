package radon.jujutsu_kaisen.data.capability;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
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

public class JujutsuCapability implements IJujutsuCapability {
    private final LivingEntity owner;

    public JujutsuCapability(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public ISorcererData getSorcererData() {
        return this.owner.getData(JJKAttachmentTypes.SORCERER);
    }

    @Override
    public IAbilityData getAbilityData() {
        return this.owner.getData(JJKAttachmentTypes.ABILITY);
    }

    @Override
    public IChantData getChantData() {
        return this.owner.getData(JJKAttachmentTypes.CHANT);
    }

    @Override
    public IContractData getContractData() {
        return this.owner.getData(JJKAttachmentTypes.CONTRACT);
    }

    @Override
    public ITenShadowsData getTenShadowsData() {
        return this.owner.getData(JJKAttachmentTypes.TEN_SHADOWS);
    }

    @Override
    public ICurseManipulationData getCurseManipulationData() {
        return this.owner.getData(JJKAttachmentTypes.CURSE_MANIPULATION);
    }

    @Override
    public IProjectionSorceryData getProjectionSorceryData() {
        return this.owner.getData(JJKAttachmentTypes.PROJECTION_SORCERY);
    }

    @Override
    public IIdleTransfigurationData getIdleTransfigurationData() {
        return this.owner.getData(JJKAttachmentTypes.IDLE_TRANSFIGURATION);
    }

    @Override
    public IMimicryData getMimicryData() {
        return this.owner.getData(JJKAttachmentTypes.MIMICRY);
    }

    @Override
    public ICursedSpeechData getCursedSpeechData() {
        return this.owner.getData(JJKAttachmentTypes.CURSED_SPEECH);
    }

    @Override
    public ISkillData getSkillData() {
        return this.owner.getData(JJKAttachmentTypes.SKILL);
    }

    @Override
    public IMissionEntityData getMissionData() {
        return this.owner.getData(JJKAttachmentTypes.MISSION_ENTITY);
    }
}
