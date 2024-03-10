package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.data.ten_shadows.TenShadowsMode;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

public class AbilityMode extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return false;

        IAbilityData ownerAbilityData = ownerCap.getAbilityData();
        ITenShadowsData ownerTenShadowsData = ownerCap.getTenShadowsData();

        IJujutsuCapability targetCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (targetCap != null) {
            ISorcererData targetSorcererData = targetCap.getSorcererData();
            IAbilityData targetAbilityData = targetCap.getAbilityData();

            if (ownerTenShadowsData.hasTamed(JJKEntities.MAHORAGA.get())) {
                if (targetAbilityData.hasToggled(JJKAbilities.INFINITY.get())) {
                    return !ownerTenShadowsData.isAdaptedTo(JJKAbilities.INFINITY.get());
                }

                if (targetSorcererData.getTechnique() != null && !ownerTenShadowsData.isAdaptedTo(targetSorcererData.getTechnique())) {
                    return true;
                }
            } else {
                if (targetAbilityData.hasToggled(JJKAbilities.INFINITY.get())) {
                    return ownerTenShadowsData.isAdaptedTo(JJKAbilities.INFINITY.get());
                }

                if (targetSorcererData.getTechnique() != null && ownerTenShadowsData.isAdaptedTo(targetSorcererData.getTechnique())) {
                    return false;
                }
            }
        }

        if (ownerAbilityData.hasToggled(this)) {
            return owner.level().getGameTime() % 20 != 0 || HelperMethods.RANDOM.nextInt(10) != 0;
        }
        return HelperMethods.RANDOM.nextInt(40) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.CURSED_ENERGY_FLOW.get();
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.abilityModeCost.get();
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}
