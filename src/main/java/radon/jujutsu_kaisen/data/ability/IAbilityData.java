package radon.jujutsu_kaisen.data.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.IAttack;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.ICharged;
import radon.jujutsu_kaisen.ability.IDomainAttack;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.IToggled;

import java.util.Set;

public interface IAbilityData extends INBTSerializable<CompoundTag> {
    void tick();

    void attack(DamageSource source, LivingEntity target);

    boolean hasActive(Ability ability);

    void toggle(Ability ability);

    boolean hasToggled(Ability ability);

    void clear();

    Set<Ability> getToggled();

    @Nullable Ability getChanneled();

    void channel(@Nullable Ability ability);

    boolean isChanneling(Ability ability);

    int getCharge();

    void addCooldown(Ability ability);

    void removeCooldown(Ability ability);

    int getRemainingCooldown(Ability ability);

    boolean isCooldownDone(Ability ability);

    void resetCooldowns();

    void disrupt(Ability ability, int duration);

    void addDuration(Ability ability);

    void delayTickEvent(Runnable task, int delay);
}
