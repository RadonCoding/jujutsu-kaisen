package radon.jujutsu_kaisen.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;

import java.util.function.Consumer;

public interface ISorcererData {
    void tick(LivingEntity owner);
    boolean isInitialized();
    void generate();

    boolean hasToggledAbility(Ability ability);
    CursedTechnique getTechnique();
    SorcererGrade getGrade();
    void setGrade(SorcererGrade grade);
    SpecialTrait getTrait();
    void setTrait(SpecialTrait trait);
    void addExperience(LivingEntity owner, float amount);
    void toggleAbility(LivingEntity owner, Ability ability);

    void addCooldown(LivingEntity owner, Ability ability);
    int getRemainingCooldown(Ability ability);
    boolean isCooldownDone(Ability ability);

    void setBurnout(int duration);
    int getBurnout();
    boolean hasBurnout();

    float getEnergy();
    float getMaxEnergy();
    void useEnergy(float amount);

    void delayTickEvent(Consumer<LivingEntity> task, int delay);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
