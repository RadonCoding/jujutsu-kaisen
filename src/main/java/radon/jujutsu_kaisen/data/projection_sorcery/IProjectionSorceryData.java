package radon.jujutsu_kaisen.data.projection_sorcery;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.AbstractMap;
import java.util.List;

public interface IProjectionSorceryData extends INBTSerializable<CompoundTag> {
    void tick();

    List<AbstractMap.SimpleEntry<Vec3, Float>> getFrames();

    void addFrame(Vec3 frame, float yaw);

    void removeFrame(AbstractMap.SimpleEntry<Vec3, Float> frame);

    void resetFrames();

    int getSpeedStacks();

    void addSpeedStack();

    void resetSpeedStacks();
}
