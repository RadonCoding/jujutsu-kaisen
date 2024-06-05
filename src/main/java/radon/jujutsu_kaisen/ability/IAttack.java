package radon.jujutsu_kaisen.ability;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface IAttack {
    boolean attack(DamageSource source, LivingEntity owner, LivingEntity target);
}
