package radon.jujutsu_kaisen.data.mimicry;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import java.util.Set;

public interface IMimicryData extends INBTSerializable<CompoundTag> {
    void tick();

    void copy(CursedTechnique technique);

    void copy(Set<CursedTechnique> techniques);

    void uncopy(CursedTechnique technique);

    boolean hasCopied(CursedTechnique technique);

    Set<CursedTechnique> getCopied();

    @Nullable CursedTechnique getCurrentCopied();

    void setCurrentCopied(@Nullable CursedTechnique technique);
}
