package radon.jujutsu_kaisen.data.mission;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.gui.screen.DisplayItem;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class Mission {
    private boolean initialized;
    private final ResourceKey<Level> dimension;
    private final MissionType type;
    private final MissionGrade grade;
    private final BlockPos pos;
    private final Set<UUID> curses;

    public Mission(ResourceKey<Level> dimension, MissionType type, MissionGrade grade, BlockPos pos) {
        this.dimension = dimension;
        this.type = type;
        this.grade = grade;
        this.pos = pos;
        this.curses = new HashSet<>();
    }

    public Mission(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");
        this.dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("dimension")));
        this.type = MissionType.values()[nbt.getInt("type")];
        this.grade = MissionGrade.values()[nbt.getInt("grade")];
        this.pos = NbtUtils.readBlockPos(nbt.getCompound("pos"));

        this.curses = new HashSet<>();

        for (Tag key : nbt.getList("curses", Tag.TAG_INT_ARRAY)) {
            this.curses.add(NbtUtils.loadUUID(key));
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public MissionType getType() {
        return this.type;
    }

    public MissionGrade getGrade() {
        return this.grade;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Set<UUID> getCurses() {
        return this.curses;
    }

    public void addCurse(UUID identifier) {
        this.curses.add(identifier);
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("initialized", this.initialized);
        nbt.putString("dimension", this.dimension.location().toString());
        nbt.putInt("type", this.type.ordinal());
        nbt.putInt("grade", this.grade.ordinal());
        nbt.put("pos", NbtUtils.writeBlockPos(this.pos));

        ListTag cursesTag = new ListTag();

        for (UUID identifier : this.curses) {
            cursesTag.add(NbtUtils.createUUID(identifier));
        }
        nbt.put("curses", cursesTag);

        return nbt;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Mission other)) {
            return false;
        }
        return this.pos.equals(other.pos);
    }

    @Override
    public int hashCode() {
        return this.pos.hashCode();
    }
}
