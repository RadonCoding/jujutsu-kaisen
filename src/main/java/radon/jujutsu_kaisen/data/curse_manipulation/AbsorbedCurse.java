package radon.jujutsu_kaisen.data.curse_manipulation;


import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class AbsorbedCurse {
    private final Component name;
    private final EntityType<?> type;
    private final CompoundTag data;

    @Nullable
    private GameProfile profile;

    public AbsorbedCurse(Component name, EntityType<?> type, CompoundTag data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public AbsorbedCurse(Component name, EntityType<?> type, CompoundTag data, @Nullable GameProfile profile) {
        this(name, type, data);

        this.profile = profile;
    }

    public AbsorbedCurse(CompoundTag nbt) {
        this.name = Component.Serializer.fromJson(nbt.getString("name"));
        this.type = EntityType.byString(nbt.getString("type")).orElseThrow();
        this.data = nbt.getCompound("data");

        if (nbt.contains("profile")) {
            this.profile = NbtUtils.readGameProfile(nbt.getCompound("profile"));
        }
    }

    public Component getName() {
        return this.name;
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public CompoundTag getData() {
        return this.data;
    }

    @Nullable
    public GameProfile getProfile() {
        return this.profile;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("name", Component.Serializer.toJson(this.name));
        nbt.putString("type", EntityType.getKey(this.type).toString());
        nbt.put("data", this.data);

        if (this.profile != null) {
            nbt.put("profile", NbtUtils.writeGameProfile(new CompoundTag(), this.profile));
        }
        return nbt;
    }
}
