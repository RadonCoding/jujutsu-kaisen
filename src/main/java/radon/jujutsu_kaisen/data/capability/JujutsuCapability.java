package radon.jujutsu_kaisen.data.capability;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;

import javax.annotation.Nullable;

public class JujutsuCapability implements IJujutsuCapability {
    private final LivingEntity owner;

    public JujutsuCapability(LivingEntity owner) {
        this.owner = owner;
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
    public ISorcererData getSorcererData() {
        return this.owner.getData(JJKAttachmentTypes.SORCERER);
    }

    @Override
    public ITenShadowsData getTenShadowsData() {
        return this.owner.getData(JJKAttachmentTypes.TEN_SHADOWS);
    }
}
