package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class Mahoraga extends Summon<MahoragaEntity> {
    public Mahoraga() {
        super(MahoragaEntity.class);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;

        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return false;

        IAbilityData ownerAbilityData = ownerCap.getAbilityData();
        ITenShadowsData ownerTenShadowsData = ownerCap.getTenShadowsData();

        if (!this.isTamed(owner)) {
            return target.getHealth() > owner.getHealth() * 4 || owner.getHealth() / owner.getMaxHealth() <= 0.1F;
        }

        if (ownerAbilityData.hasToggled(this)) {
            return true;
        }

        IJujutsuCapability targetCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (targetCap == null) return false;

        ISorcererData targetData = targetCap.getSorcererData();

        for (ICursedTechnique technique : targetData.getActiveTechniques()) {
            if (ownerTenShadowsData.isAdaptedTo(technique)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isNotDisabledFromUV(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData abilityData = cap.getAbilityData();
        ITenShadowsData tenShadowsData = cap.getTenShadowsData();

        if (abilityData.hasToggled(this)) return super.isNotDisabledFromUV(owner);

        if (tenShadowsData.isAdaptedTo(JJKAbilities.UNLIMITED_VOID.get())) return true;

        return super.isNotDisabledFromUV(owner);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return this.isTamed(owner) ? ActivationType.TOGGLED : ActivationType.INSTANT;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return this.isTamed(owner) ? 0.3F : 500.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.MAHORAGA.get());
    }

    @Override
    protected boolean canTame() {
        return true;
    }

    @Override
    public boolean canDie() {
        return true;
    }

    @Override
    public boolean isTenShadows() {
        return true;
    }

    @Override
    protected MahoragaEntity summon(LivingEntity owner) {
        return new MahoragaEntity(owner, this.isTamed(owner));
    }
}
