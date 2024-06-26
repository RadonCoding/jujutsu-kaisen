package radon.jujutsu_kaisen.data.mission;


import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class Mission {
    private final ResourceKey<Level> dimension;
    private final MissionType type;
    private final MissionGrade grade;
    private final BlockPos pos;
    private final Set<UUID> curses;
    private final CopyOnWriteArraySet<BlockPos> spawns;
    private int total;

    public Mission(ResourceKey<Level> dimension, MissionType type, MissionGrade grade, BlockPos pos) {
        this.dimension = dimension;
        this.type = type;
        this.grade = grade;
        this.pos = pos;
        this.curses = new HashSet<>();
        this.spawns = new CopyOnWriteArraySet<>();
    }

    public Mission(CompoundTag nbt) {
        this.dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("dimension")));
        this.type = MissionType.values()[nbt.getInt("type")];
        this.grade = MissionGrade.values()[nbt.getInt("grade")];
        this.pos = NbtUtils.readBlockPos(nbt, "pos").orElseThrow();

        this.curses = new HashSet<>();

        for (Tag key : nbt.getList("curses", Tag.TAG_INT_ARRAY)) {
            this.curses.add(NbtUtils.loadUUID(key));
        }

        this.spawns = new CopyOnWriteArraySet<>();

        for (Tag tag : nbt.getList("spawns", Tag.TAG_INT_ARRAY)) {
            int[] data = ((IntArrayTag) tag).getAsIntArray();
            this.spawns.add(new BlockPos(data[0], data[1], data[2]));
        }

        this.total = nbt.getInt("total");
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

        this.total = Math.max(this.total, this.curses.size());
    }

    public Set<BlockPos> getSpawns() {
        return this.spawns;
    }

    public void addSpawn(BlockPos pos) {
        this.spawns.add(pos);
    }

    public int getTotal() {
        return this.total;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("dimension", this.dimension.location().toString());
        nbt.putInt("type", this.type.ordinal());
        nbt.putInt("grade", this.grade.ordinal());
        nbt.put("pos", NbtUtils.writeBlockPos(this.pos));

        ListTag cursesTag = new ListTag();

        for (UUID identifier : this.curses) {
            cursesTag.add(NbtUtils.createUUID(identifier));
        }
        nbt.put("curses", cursesTag);

        ListTag spawnsTag = new ListTag();

        for (BlockPos pos : this.spawns) {
            spawnsTag.add(NbtUtils.writeBlockPos(pos));
        }
        nbt.put("spawns", spawnsTag);

        nbt.putInt("total", this.total);

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
