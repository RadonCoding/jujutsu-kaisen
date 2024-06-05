package radon.jujutsu_kaisen.entity;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;

public interface ICommandable {
    boolean canChangeTarget();

    void changeTarget(LivingEntity target);
}
