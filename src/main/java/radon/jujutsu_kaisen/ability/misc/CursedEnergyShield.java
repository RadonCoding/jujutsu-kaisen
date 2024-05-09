package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;

public class CursedEnergyShield extends Ability implements IChanneled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public boolean isChantable() {
        return false;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.NONE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel)) return;

        owner.addEffect(new MobEffectInstance(JJKEffects.STUN, 2, 1, false, false, false));
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();
        return data.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get()) && super.isValid(owner);
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 3.0F;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return true;
    }
}
