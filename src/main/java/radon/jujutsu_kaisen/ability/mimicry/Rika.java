package radon.jujutsu_kaisen.ability.mimicry;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.List;

public class Rika extends Summon<RikaEntity> {
    public Rika() {
        super(RikaEntity.class);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return false;

        IAbilityData ownerData = ownerCap.getAbilityData();

        if (ownerData.hasToggled(this)) return target != null;

        if (target != null) {
            if (owner.getHealth() / owner.getMaxHealth() <= 0.5F) return true;

            IJujutsuCapability targetCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (targetCap == null) return false;

            ISorcererData targetData = targetCap.getSorcererData();

            return SorcererUtil.getGrade(targetData.getExperience()).ordinal() > SorcererGrade.GRADE_1.ordinal();
        }
        return false;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.RIKA.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    protected RikaEntity summon(LivingEntity owner) {
        return new RikaEntity(owner);
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }
}
