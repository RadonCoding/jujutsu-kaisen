package radon.jujutsu_kaisen.data.mission.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.UnknownNullability;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.tags.JJKStructureTags;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MissionEntityData implements IMissionEntityData {
    @Nullable
    private Mission mission;

    private final LivingEntity owner;

    public MissionEntityData(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public void tick() {
        if (!(this.owner.level() instanceof ServerLevel level)) return;

        if (this.mission == null || this.owner.level().dimension() != this.mission.getDimension()) return;

        BlockPos pos = this.mission.getPos();

        if (!level.hasChunk(pos.getX(), pos.getZ())) return;
        // If all the curses are dead then the mission is completed
        Set<UUID> curses = this.mission.getCurses();

        if (curses.isEmpty()) {
            this.owner.sendSystemMessage(Component.literal("Completed mission!"));

            // Completed give player rewards or something
            this.mission = null;
            return;
        }

        Iterator<UUID> cursesIter = curses.iterator();

        while (cursesIter.hasNext()) {
            UUID identifier = cursesIter.next();

            Entity curse = level.getEntity(identifier);

            if (curse == null) cursesIter.remove();
        }
    }

    @Override
    public @Nullable Mission getMission() {
        return this.mission;
    }

    @Override
    public void setMission(@Nullable Mission mission) {
        this.mission = mission;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        if (this.mission != null) {
            nbt.put("mission", this.mission.serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("mission")) {
            this.mission = new Mission(nbt.getCompound("mission"));
        }
    }
}
