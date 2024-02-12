package radon.jujutsu_kaisen.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.effect.base.JJKEffect;

import java.util.HashMap;
import java.util.UUID;

public class CursedBudEffect extends JJKEffect {
    private static final HashMap<UUID, Float> AMOUNTS = new HashMap<>();

    protected CursedBudEffect(MobEffectCategory pCategory, int pColor) {
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
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        super.applyEffectTick(pLivingEntity, pAmplifier);

        if (pLivingEntity.level().isClientSide) return;

        if (pLivingEntity.isDeadOrDying()) return;

        IJujutsuCapability cap = pLivingEntity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        MobEffectInstance instance = pLivingEntity.getEffect(this);

        if (instance != null && instance.endsWithin(0)) {
            AMOUNTS.remove(pLivingEntity.getUUID());
        }

        float previous = AMOUNTS.getOrDefault(pLivingEntity.getUUID(), data.getEnergy());

        if (previous > data.getEnergy()) {
            pLivingEntity.hurt(pLivingEntity.level().damageSources().generic(), (previous - data.getEnergy()) * 0.5F);
        }
        AMOUNTS.put(pLivingEntity.getUUID(), data.getEnergy());
    }
}