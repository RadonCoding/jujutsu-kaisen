package radon.jujutsu_kaisen.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TwentyFourFrameRuleEffect extends JJKEffect {
    private final Map<UUID, Vec3> positions = new HashMap<>();

    protected TwentyFourFrameRuleEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        super.applyEffectTick(pLivingEntity, pAmplifier);

        if (pLivingEntity.level().isClientSide) return;

        MobEffectInstance instance = pLivingEntity.getEffect(JJKEffects.TWENTY_FOUR_FRAME_RULE.get());

        if (instance != null && instance.getDuration() == 0) {
            this.positions.remove(pLivingEntity.getUUID());
            return;
        }

        if (this.positions.containsKey(pLivingEntity.getUUID()) && pLivingEntity.position() != this.positions.get(pLivingEntity.getUUID())) {
            Vec3 center = new Vec3(pLivingEntity.getX(), pLivingEntity.getY() + (pLivingEntity.getBbHeight() / 2.0F), pLivingEntity.getZ());
            ((ServerLevel) pLivingEntity.level()).sendParticles(ParticleTypes.EXPLOSION, center.x(), center.y(), center.z(), 0, 1.0D, 0.0D, 0.0D, 1.0D);

            pLivingEntity.hurt(JJKDamageSources.jujutsuAttack(pLivingEntity, JJKAbilities.TIME_CELL_MOON_PALACE.get()), 10.0F);
        }
        this.positions.put(pLivingEntity.getUUID(), pLivingEntity.position());
    }
}
