package radon.jujutsu_kaisen.ability;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;

public interface ICharged extends IChanneled {
    default boolean onRelease(LivingEntity owner) {
            return true;
        }
}
