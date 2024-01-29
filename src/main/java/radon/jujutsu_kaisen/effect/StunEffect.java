package radon.jujutsu_kaisen.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.effect.base.JJKEffect;

import java.util.HashMap;
import java.util.UUID;

public class StunEffect extends JJKEffect {
    private static final HashMap<UUID, Vec3> POSITIONS = new HashMap<>();

    protected StunEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void addAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);

        POSITIONS.put(pLivingEntity.getUUID(), pLivingEntity.position());
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);

        POSITIONS.remove(pLivingEntity.getUUID());
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        super.applyEffectTick(pLivingEntity, pAmplifier);

        if (pLivingEntity.level().isClientSide) return;

        Vec3 pos = POSITIONS.get(pLivingEntity.getUUID());
        pLivingEntity.teleportTo(pos.x, pos.y, pos.z);
    }
}