package radon.jujutsu_kaisen.data.idle_transfiguration;

import net.minecraft.nbt.CompoundTag;
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
    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("transfigured_souls", this.transfiguredSouls);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.transfiguredSouls = nbt.getInt("transfigured_souls");
    }
}
