package radon.jujutsu_kaisen.ability;


import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface IAttack {
    boolean attack(DamageSource source, LivingEntity owner, LivingEntity target);
}
