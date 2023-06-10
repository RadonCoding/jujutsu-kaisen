package radon.jujutsu_kaisen.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import radon.jujutsu_kaisen.ability.Ability;

import java.util.function.Consumer;

public interface ISorcererData {
    void tick(LivingEntity entity);
    boolean isInitialized();
    void generate();

    boolean hasToggledAbility(Ability ability);
    CursedTechnique getTechnique();
    SorcererGrade getGrade();
    SpecialTrait getTrait();
    void addExperience(float amount);
    void toggleAbility(LivingEntity entity, Ability ability);

    void addCooldown(Ability ability);
    int getRemainingCooldown(Ability ability);
    boolean isCooldownDone(Ability ability);

    float getEnergy();
    float getMaxEnergy();
    void useEnergy(float amount);

    void delayTickEvent(Consumer<LivingEntity> task, int delay);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
