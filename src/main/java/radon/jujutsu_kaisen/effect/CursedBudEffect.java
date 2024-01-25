package radon.jujutsu_kaisen.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.HashMap;
import java.util.UUID;

public class CursedBudEffect extends JJKEffect {
    private static final HashMap<UUID, Float> AMOUNTS = new HashMap<>();

    protected CursedBudEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void addAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);

        if (!pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

        ISorcererData cap = pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        AMOUNTS.put(pLivingEntity.getUUID(), cap.getEnergy());
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);

        AMOUNTS.remove(pLivingEntity.getUUID());
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        super.applyEffectTick(pLivingEntity, pAmplifier);

        if (pLivingEntity.level().isClientSide) return;

        if (!pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

        ISorcererData cap = pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        float previous = AMOUNTS.getOrDefault(pLivingEntity.getUUID(), cap.getEnergy());

        if (previous > cap.getEnergy()) {
            pLivingEntity.hurt(pLivingEntity.level().damageSources().generic(), (previous - cap.getEnergy()) * 0.1F);
        }
        AMOUNTS.put(pLivingEntity.getUUID(), cap.getEnergy());
    }
}
