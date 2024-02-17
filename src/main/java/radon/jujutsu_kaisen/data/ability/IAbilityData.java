package radon.jujutsu_kaisen.data.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;

import java.util.Set;

public interface IAbilityData extends INBTSerializable<CompoundTag> {
    void tick();

    void attack(DamageSource source, LivingEntity target);

    void toggle(Ability ability);

    boolean hasToggled(Ability ability);

    void clearToggled();

    Set<Ability> getToggled();

    @Nullable Ability getChanneled();

    void channel(@Nullable Ability ability);

    boolean isChanneling(Ability ability);

    int getCharge();

    void addCooldown(Ability ability);

    int getRemainingCooldown(Ability ability);

    boolean isCooldownDone(Ability ability);

    void resetCooldowns();

    void disrupt(Ability ability, int duration);

    void addDuration(Ability ability);

    void delayTickEvent(Runnable task, int delay);
}
