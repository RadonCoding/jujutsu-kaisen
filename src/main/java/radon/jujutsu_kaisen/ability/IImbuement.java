package radon.jujutsu_kaisen.ability;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;

public interface IImbuement {
    void hit(LivingEntity owner, LivingEntity target);
}
