package radon.jujutsu_kaisen.data.cursed_speech;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class CursedSpeechData implements ICursedSpeechData {
    private int throatDamage;

    @Override
    public void tick() {
        if (this.throatDamage > 0) {
            this.throatDamage--;
        }
    }

    @Override
    public void hurtThroat(int damage) {
        this.throatDamage = damage;
    }

    @Override
    public int getThroatDamage() {
        return this.throatDamage;
    }

    @Override
    public boolean isThroatDamaged() {
        return this.throatDamage > 0;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("throat_damage", this.throatDamage);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        nbt.putInt("throat_damage", this.throatDamage);
    }
}
