package radon.jujutsu_kaisen.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import radon.jujutsu_kaisen.ability.Ability;

public interface ISorcererData {
    void tick(LivingEntity entity, boolean isClientSide);
    boolean isInitialized();
    void generate();

    boolean hasToggled(Ability ability);
    CursedTechnique getTechnique();
    SorcererGrade getGrade();
    void toggleAbility(Ability ability);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
