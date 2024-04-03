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

    public Mission(MissionType type, MissionGrade grade, BlockPos pos) {
        this.type = type;
        this.grade = grade;
        this.pos = pos;
    }

    public Mission(CompoundTag nbt) {
        this.type = MissionType.values()[nbt.getInt("type")];
        this.grade = MissionGrade.values()[nbt.getInt("grade")];
        this.pos = NbtUtils.readBlockPos(nbt.getCompound("pos"));
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

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("type", this.type.ordinal());
        nbt.putInt("grade", this.grade.ordinal());
        nbt.put("pos", NbtUtils.writeBlockPos(this.pos));
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
