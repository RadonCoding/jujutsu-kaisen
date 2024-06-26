package radon.jujutsu_kaisen.data.cursed_speech;


import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface ICursedSpeechData extends INBTSerializable<CompoundTag> {
    void tick();

    void hurtThroat(int cooldown);

    int getThroatDamage();

    boolean isThroatDamaged();
}
