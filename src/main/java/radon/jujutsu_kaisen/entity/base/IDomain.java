package radon.jujutsu_kaisen.entity.base;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;

public interface IDomain extends IBarrier {
    default float getStrength() {
        LivingEntity owner = this.getOwner();

        if (owner == null) return 0.0F;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISkillData data = cap.getSkillData();

        return (data.getSkill(Skill.BARRIER) * 0.01F) * (owner.getHealth() / owner.getMaxHealth());
    }
}
