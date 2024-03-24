package radon.jujutsu_kaisen.entity.base;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;

public interface IDomain extends IBarrier {
    float getScale();

    default float getStrength() {
        return IBarrier.super.getStrength() * DomainExpansion.getStrength(false, this.getScale());
    }
}
