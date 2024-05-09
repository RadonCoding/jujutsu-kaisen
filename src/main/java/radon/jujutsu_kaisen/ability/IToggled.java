package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;

public interface IToggled {
    void onEnabled(LivingEntity owner);

    void onDisabled(LivingEntity owner);

    default void applyModifiers(LivingEntity owner) {}

    default void removeModifiers(LivingEntity owner) {}
}
