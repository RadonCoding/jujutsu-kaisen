package radon.jujutsu_kaisen.item.veil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class Modifier {
    private final Modifier.Type type;

    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("type", this.type.ordinal());
        return nbt;
    }

    public Type getType() {
        return this.type;
    }

    public Component getComponent() {
        return Component.empty();
    }

    public Modifier(Modifier.Type type) {
        this.type = type;
    }

    public Modifier(CompoundTag nbt) {
        this.type = Modifier.Type.values()[nbt.getInt("type")];
    }

    public enum Type {
        NONE,
        PLAYER_BLACKLIST,
        ENTITY_BLACKLIST,
        COLOR,
        TRANSPARENT
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Modifier other)) {
            return false;
        }
        return this.type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }
}
