package radon.jujutsu_kaisen.data.projection_sorcery;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JJKConstants;
import radon.jujutsu_kaisen.util.EntityUtil;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectionSorceryData implements IProjectionSorceryData {
    private static final UUID PROJECTION_SORCERY_MOVEMENT_SPEED_UUID = UUID.fromString("23ecaba3-fbe8-44c1-93c4-5291aa9ee777");
    private static final UUID PROJECTION_ATTACK_SPEED_UUID = UUID.fromString("18cd1e25-656d-4172-b9f7-2f1b3daf4b89");
    private static final UUID PROJECTION_STEP_HEIGHT_UUID = UUID.fromString("1dbcbef7-8193-406a-b64d-8766ea505fdb");

    private final List<AbstractMap.SimpleEntry<Vec3, Float>> frames;
    private int speedStacks;
    private int noMotionTime;

    private final LivingEntity owner;

    public ProjectionSorceryData(LivingEntity owner) {
        this.owner = owner;

        this.frames = new ArrayList<>();
    }

    @Override
    public void tick() {
        if (!this.owner.level().isClientSide) {
            if (this.speedStacks > 0) {
                EntityUtil.applyModifier(this.owner, Attributes.MOVEMENT_SPEED, PROJECTION_SORCERY_MOVEMENT_SPEED_UUID, "Movement speed", this.speedStacks * 2.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                EntityUtil.applyModifier(this.owner, Attributes.ATTACK_SPEED, PROJECTION_ATTACK_SPEED_UUID, "Attack speed", this.speedStacks, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                EntityUtil.applyModifier(this.owner, Attributes.STEP_HEIGHT, PROJECTION_STEP_HEIGHT_UUID, "Step height addition", 2.0F, AttributeModifier.Operation.ADD_VALUE);

                if (this.owner.walkDist == this.owner.walkDistO) {
                    this.noMotionTime++;
                } else if (this.noMotionTime == 1) {
                    this.noMotionTime = 0;
                }

                if (this.noMotionTime > 1) {
                    this.resetSpeedStacks();
                }
            } else {
                EntityUtil.removeModifier(this.owner, Attributes.MOVEMENT_SPEED, PROJECTION_SORCERY_MOVEMENT_SPEED_UUID);
                EntityUtil.removeModifier(this.owner, Attributes.ATTACK_SPEED, PROJECTION_ATTACK_SPEED_UUID);
                EntityUtil.removeModifier(this.owner, Attributes.STEP_HEIGHT, PROJECTION_STEP_HEIGHT_UUID);
            }
        }
    }

    @Override
    public List<AbstractMap.SimpleEntry<Vec3, Float>> getFrames() {
        return this.frames;
    }

    @Override
    public void addFrame(Vec3 frame, float yaw) {
        this.frames.add(new AbstractMap.SimpleEntry<>(frame, yaw));
    }

    @Override
    public void removeFrame(AbstractMap.SimpleEntry<Vec3, Float> frame) {
        this.frames.remove(frame);
    }

    @Override
    public void resetFrames() {
        this.frames.clear();
    }

    @Override
    public int getSpeedStacks() {
        return this.speedStacks;
    }

    @Override
    public void addSpeedStack() {
        this.speedStacks = Math.min(JJKConstants.MAX_PROJECTION_SORCERY_STACKS, this.speedStacks + 1);
    }

    @Override
    public void resetSpeedStacks() {
        this.speedStacks = 0;
        this.noMotionTime = 0;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {

    }
}
