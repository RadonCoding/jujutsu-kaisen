package radon.jujutsu_kaisen.ability;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public interface ITenShadowsAttack {
    void perform(LivingEntity owner, @Nullable LivingEntity target);
}