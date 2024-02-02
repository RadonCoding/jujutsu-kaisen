package radon.jujutsu_kaisen.capability.data.curse_manipulation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.capabilities.AutoRegisterCapability;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.AbstractMap;
import java.util.List;
import java.util.Set;

@AutoRegisterCapability
public interface ICurseManipulationData {
    void tick(LivingEntity owner);

    void init(LivingEntity owner);

    void absorb(@Nullable ICursedTechnique technique);

    void unabsorb(ICursedTechnique technique);

    Set<ICursedTechnique> getAbsorbed();

    void setCurrentAbsorbed(@Nullable ICursedTechnique technique);

    @Nullable ICursedTechnique getCurrentAbsorbed();

    void addCurse(AbsorbedCurse curse);

    void removeCurse(AbsorbedCurse curse);

    List<AbsorbedCurse> getCurses();

    @Nullable
    AbsorbedCurse getCurse(EntityType<?> type);

    boolean hasCurse(EntityType<?> type);

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag nbt);
}
