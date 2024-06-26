package radon.jujutsu_kaisen.item.veil.modifier;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class Modifier {
    private final Modifier.Type type;
    private final Modifier.Action action;

    public Modifier(Modifier.Type type, Action action) {
        this.type = type;
        this.action = action;
    }

    public Modifier(CompoundTag nbt) {
        this.type = Modifier.Type.values()[nbt.getInt("type")];
        this.action = Modifier.Action.values()[nbt.getInt("action")];
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("type", this.type.ordinal());
        nbt.putInt("action", this.action.ordinal());
        return nbt;
    }

    public Type getType() {
        return this.type;
    }

    public Action getAction() {
        return this.action;
    }

    public Component getComponent() {
        return Component.empty();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Modifier other)) {
            return false;
        }
        return this.type == other.type;
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    public enum Type {
        NONE,
        PLAYER,
        COLOR,
        TRANSPARENT,
        CURSE,
        SORCERER,
        GRIEFING,
        VIOLENCE
    }

    public enum Action {
        NONE,
        ALLOW,
        DENY
    }
}
