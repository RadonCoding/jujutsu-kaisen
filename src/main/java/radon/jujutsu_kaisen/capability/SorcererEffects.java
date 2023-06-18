package radon.jujutsu_kaisen.capability;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class SorcererEffects {
    public static void apply(LivingEntity entity, SorcererGrade grade) {
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, Math.min(3, grade.ordinal()),
                false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2, Math.min(4, grade.ordinal()),
                false, false, false));
    }
}
