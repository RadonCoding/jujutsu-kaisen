package radon.jujutsu_kaisen.ability;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;

public interface IToggled {
    void onEnabled(LivingEntity owner);

    void onDisabled(LivingEntity owner);

    default void applyModifiers(LivingEntity owner) {}

    default void removeModifiers(LivingEntity owner) {}
}
