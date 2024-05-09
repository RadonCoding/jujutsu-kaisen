package radon.jujutsu_kaisen.data.mimicry;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.cursed_technique.ICursedTechnique;

import java.util.Set;

public interface IMimicryData extends INBTSerializable<CompoundTag> {
    void tick();

    void copy(ICursedTechnique technique);

    void copy(Set<ICursedTechnique> techniques);

    void uncopy(ICursedTechnique technique);

    boolean hasCopied(ICursedTechnique technique);

    Set<ICursedTechnique> getCopied();

    @Nullable ICursedTechnique getCurrentCopied();

    void setCurrentCopied(@Nullable ICursedTechnique technique);
}
