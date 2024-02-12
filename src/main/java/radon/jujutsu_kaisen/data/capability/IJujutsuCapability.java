package radon.jujutsu_kaisen.data.capability;

import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;

public interface IJujutsuCapability {
    ICurseManipulationData getCurseManipulationData();

    IProjectionSorceryData getProjectionSorceryData();

    ISorcererData getSorcererData();

    ITenShadowsData getTenShadowsData();
}
