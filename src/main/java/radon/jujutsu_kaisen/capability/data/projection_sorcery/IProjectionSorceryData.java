package radon.jujutsu_kaisen.capability.data.projection_sorcery;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.capabilities.AutoRegisterCapability;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ten_shadows.Adaptation;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AutoRegisterCapability
public interface IProjectionSorceryData {
    void tick(LivingEntity owner);

    void init(LivingEntity owner);

    List<AbstractMap.SimpleEntry<Vec3, Float>> getFrames();
    void addFrame(Vec3 frame, float yaw);
    void removeFrame(AbstractMap.SimpleEntry<Vec3, Float> frame);
    void resetFrames();

    int getSpeedStacks();
    void addSpeedStack();
    void resetSpeedStacks();

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
