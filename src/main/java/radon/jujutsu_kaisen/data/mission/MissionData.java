package radon.jujutsu_kaisen.data.mission;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.SkyLightEngine;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.tags.JJKEntityTypeTags;
import radon.jujutsu_kaisen.tags.JJKStructureTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.*;

public class MissionData implements IMissionData {
    private final Set<Mission> missions;

    private final Level level;

    public MissionData(Level level) {
        this.level = level;

        this.missions = new LinkedHashSet<>();
    }

    @Override
    public void tick() {
        if (!(this.level instanceof ServerLevel serverLevel)) return;

        for (Mission mission : this.missions) {
            if (mission.isSpawned()) continue;

            List<EntityType<?>> spawns = new ArrayList<>();
            List<EntityType<?>> bosses = new ArrayList<>();

            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                if (!type.is(JJKEntityTypeTags.SPAWNABLE_CURSE)) continue;

                if (!((type.create(this.level)) instanceof CursedSpirit curse)) continue;

                int diff = mission.getGrade().toSorcererGrade().ordinal() - curse.getGrade().ordinal();

                if (diff >= 1 && diff < 3) {
                    bosses.add(type);
                    continue;
                }

                if (curse.getGrade().ordinal() > mission.getGrade().toSorcererGrade().ordinal()) continue;

                spawns.add(type);
            }

            if (!spawns.isEmpty()) {
                for (BlockPos pos : mission.getSpawns()) {
                    spawns.get(HelperMethods.RANDOM.nextInt(spawns.size())).spawn(serverLevel, pos, MobSpawnType.SPAWNER);
                }
            }

            if (!bosses.isEmpty()) {
                for (BlockPos pos : mission.getBosses()) {
                    bosses.get(HelperMethods.RANDOM.nextInt(bosses.size())).spawn(serverLevel, pos, MobSpawnType.SPAWNER);
                }
            }
            mission.setSpawned(true);
        }
    }

    @Override
    public void register(MissionType type, MissionGrade grade, BlockPos pos) {
        this.missions.add(new Mission(type, grade, pos));
    }

    @Override
    public Set<Mission> getMissions() {
        return this.missions;
    }

    @Override
    public boolean isRegistered(BlockPos pos) {
        for (Mission mission : this.missions) {
            if (mission.getPos().equals(pos)) return true;
        }
        return false;
    }

    @Override
    public Mission getMission(BlockPos pos) {
        for (Mission mission : this.missions) {
            if (mission.getPos().equals(pos)) return mission;
        }
        return null;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag missionsTag = new ListTag();

        for (Mission mission : this.missions) {
            missionsTag.add(mission.serializeNBT());
        }
        nbt.put("missions", missionsTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        this.missions.clear();

        for (Tag tag : nbt.getList("missions", Tag.TAG_COMPOUND)) {
            this.missions.add(new Mission((CompoundTag) tag));
        }
    }
}
