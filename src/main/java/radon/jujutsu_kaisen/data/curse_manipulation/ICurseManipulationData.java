package radon.jujutsu_kaisen.data.curse_manipulation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.List;
import java.util.Set;

public interface ICurseManipulationData extends INBTSerializable<CompoundTag> {
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
}
