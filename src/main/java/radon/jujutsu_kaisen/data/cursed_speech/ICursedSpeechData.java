package radon.jujutsu_kaisen.data.cursed_speech;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface ICursedSpeechData extends INBTSerializable<CompoundTag> {
    void tick();

    void hurtThroat(int cooldown);

    int getThroatDamage();

    boolean isThroatDamaged();
}
