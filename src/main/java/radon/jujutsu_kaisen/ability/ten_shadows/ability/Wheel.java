package radon.jujutsu_kaisen.ability.ten_shadows.ability;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;

import java.util.List;

public class Wheel extends Summon<WheelEntity> {
    public Wheel() {
        super(WheelEntity.class);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    protected boolean isNotDisabledFromDA(LivingEntity owner) {
        return true;
    }

    @Override
    protected boolean isNotDisabledFromUV(LivingEntity owner) {
        return true;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (owner instanceof MahoragaEntity) return true;
        if (target == null) return false;

        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return false;

        ITenShadowsData ownerData = ownerCap.getTenShadowsData();

        IJujutsuCapability targetCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (targetCap == null) return false;

        ISorcererData targetData = targetCap.getSorcererData();

        for (CursedTechnique technique : targetData.getActiveTechniques()) {
            if (ownerData.isAdaptedTo(technique)) continue;

            return true;
        }
        return false;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!super.isValid(owner)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData abilityData = cap.getAbilityData();
        ITenShadowsData tenShadowsData = cap.getTenShadowsData();

        return !abilityData.hasToggled(JJKAbilities.MAHORAGA.get()) &&
                tenShadowsData.hasTamed(JJKEntities.MAHORAGA.get()) &&
                (abilityData.hasToggled(this) || abilityData.hasToggled(JJKAbilities.ABILITY_MODE.get()));
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.WHEEL.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    protected WheelEntity summon(LivingEntity owner) {
        return new WheelEntity(owner);
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }
}
