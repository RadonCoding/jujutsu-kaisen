package radon.jujutsu_kaisen.data.idle_transfiguration;


import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IIdleTransfigurationData extends INBTSerializable<CompoundTag> {
    void tick();

    int getTransfiguredSouls();

    void increaseTransfiguredSouls();

    void decreaseTransfiguredSouls();

    void useTransfiguredSouls(int amount);
}
