package radon.jujutsu_kaisen.effect;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;

import java.util.HashMap;
import java.util.UUID;

public class CursedBudEffect extends JJKEffect {
    private static final HashMap<UUID, Float> AMOUNTS = new HashMap<>();

    public CursedBudEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void onEffectStarted(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectStarted(pLivingEntity, pAmplifier);

        IJujutsuCapability cap = pLivingEntity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        AMOUNTS.put(pLivingEntity.getUUID(), data.getEnergy());
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        super.applyEffectTick(pLivingEntity, pAmplifier);

        if (pLivingEntity.level().isClientSide) return false;

        if (pLivingEntity.isDeadOrDying()) return false;

        IJujutsuCapability cap = pLivingEntity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        if (abilityData.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
            pLivingEntity.removeEffect(JJKEffects.CURSED_BUD);
        }

        MobEffectInstance instance = pLivingEntity.getEffect(JJKEffects.CURSED_BUD);

        if (instance != null && instance.endsWithin(0)) {
            AMOUNTS.remove(pLivingEntity.getUUID());
        }

        float previous = AMOUNTS.getOrDefault(pLivingEntity.getUUID(), sorcererData.getEnergy());

        if (previous > sorcererData.getEnergy()) {
            pLivingEntity.hurt(pLivingEntity.level().damageSources().generic(), (previous - sorcererData.getEnergy()) * 0.1F);
        }
        AMOUNTS.put(pLivingEntity.getUUID(), sorcererData.getEnergy());

        return true;
    }
}