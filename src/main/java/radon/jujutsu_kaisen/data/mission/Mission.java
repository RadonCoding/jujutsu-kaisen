package radon.jujutsu_kaisen.data.mission;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.gui.screen.DisplayItem;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Mission {
    private final MissionType type;
    private final MissionGrade grade;
    private final BlockPos pos;
    private final Set<BlockPos> spawns;
    private final Set<BlockPos> bosses;

    public Mission(MissionType type, MissionGrade grade, BlockPos pos) {
        this.type = type;
        this.grade = grade;
        this.pos = pos;
        this.spawns = new HashSet<>();
        this.bosses = new HashSet<>();
    }

    public Mission(CompoundTag nbt) {
        this.type = MissionType.values()[nbt.getInt("type")];
        this.grade = MissionGrade.values()[nbt.getInt("grade")];
        this.pos = NbtUtils.readBlockPos(nbt.getCompound("pos"));

        this.spawns = new HashSet<>();

        for (Tag tag : nbt.getList("spawns", Tag.TAG_COMPOUND)) {
            this.spawns.add(NbtUtils.readBlockPos((CompoundTag) tag));
        }

        this.bosses = new HashSet<>();

        for (Tag tag : nbt.getList("bosses", Tag.TAG_COMPOUND)) {
            this.bosses.add(NbtUtils.readBlockPos((CompoundTag) tag));
        }
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

    public Set<BlockPos> getSpawns() {
        return this.spawns;
    }

    public void addSpawn(BlockPos pos) {
        this.spawns.add(pos);
    }

    public Set<BlockPos> getBosses() {
        return this.bosses;
    }

    public void addBoss(BlockPos pos) {
        this.bosses.add(pos);
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("type", this.type.ordinal());
        nbt.putInt("grade", this.grade.ordinal());
        nbt.put("pos", NbtUtils.writeBlockPos(this.pos));

        ListTag spawnsTag = new ListTag();

        for (BlockPos pos : this.spawns) {
            spawnsTag.add(NbtUtils.writeBlockPos(pos));
        }
        nbt.put("spawns", spawnsTag);

        ListTag bossesTag = new ListTag();

        for (BlockPos pos : this.bosses) {
            bossesTag.add(NbtUtils.writeBlockPos(pos));
        }
        nbt.put("bosses", bossesTag);

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
