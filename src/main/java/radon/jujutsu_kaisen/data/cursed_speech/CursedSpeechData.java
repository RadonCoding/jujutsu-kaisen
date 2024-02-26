package radon.jujutsu_kaisen.data.cursed_speech;

import net.minecraft.nbt.CompoundTag;

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
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("throat_damage", this.throatDamage);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        nbt.putInt("throat_damage", this.throatDamage);
    }
}
