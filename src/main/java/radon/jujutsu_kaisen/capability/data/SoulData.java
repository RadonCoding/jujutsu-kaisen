package radon.jujutsu_kaisen.capability.data;

import net.minecraft.nbt.CompoundTag;

public class SoulData implements ISoulData {
    private float damage;

    @Override
    public float getDamage() {
        return this.damage;
    }

    @Override
    public void hurt(float amount) {
        this.damage += amount;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("damage", this.damage);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.damage = nbt.getFloat("damage");
    }
}
