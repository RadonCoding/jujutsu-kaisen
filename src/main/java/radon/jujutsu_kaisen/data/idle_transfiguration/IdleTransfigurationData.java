package radon.jujutsu_kaisen.data.idle_transfiguration;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class IdleTransfigurationData implements IIdleTransfigurationData {
    private int transfiguredSouls;

    @Override
    public void tick() {

    }

    @Override
    public int getTransfiguredSouls() {
        return this.transfiguredSouls;
    }

    @Override
    public void increaseTransfiguredSouls() {
        this.transfiguredSouls++;
    }

    @Override
    public void decreaseTransfiguredSouls() {
        this.transfiguredSouls--;
    }

    @Override
    public void useTransfiguredSouls(int amount) {
        this.transfiguredSouls -= amount;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("transfigured_souls", this.transfiguredSouls);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        this.transfiguredSouls = nbt.getInt("transfigured_souls");
    }
}
