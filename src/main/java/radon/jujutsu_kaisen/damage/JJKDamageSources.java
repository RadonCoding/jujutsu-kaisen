package radon.jujutsu_kaisen.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class JJKDamageSources {
    public static DamageSource jujutsuAttack(LivingEntity pMob) {
        return new JujutsuDamageSource("jujutsu", pMob);
    }

    public static DamageSource indirectJujutsuAttack(Entity pSource, @Nullable LivingEntity pIndirectEntity) {
        return new IndirectJujutsuDamageSource("jujutsu", pSource, pIndirectEntity);
    }
}
