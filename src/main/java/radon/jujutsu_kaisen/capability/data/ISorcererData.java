package radon.jujutsu_kaisen.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CurseGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;

import java.util.function.Consumer;

public interface ISorcererData {
    void tick(LivingEntity owner);
    boolean isInitialized();
    void generate();

    boolean hasToggledAbility(Ability ability);
    CursedTechnique getTechnique();
    void setTechnique(CursedTechnique technique);
    SorcererGrade getGrade();
    void setGrade(SorcererGrade grade);
    Trait getTrait();
    void setTrait(Trait trait);
    void exorcise(LivingEntity owner, CurseGrade grade);
    void toggleAbility(LivingEntity owner, Ability ability);

    void addCooldown(LivingEntity owner, Ability ability);
    int getRemainingCooldown(Ability ability);
    boolean isCooldownDone(Ability ability);

    void setBurnout(int duration);
    int getBurnout();
    boolean hasBurnout();

    void resetCooldowns();
    void resetBurnout();

    float getEnergy();
    float getMaxEnergy();
    void useEnergy(float amount);
    void setEnergy(float energy);

    void delayTickEvent(Consumer<LivingEntity> task, int delay);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
