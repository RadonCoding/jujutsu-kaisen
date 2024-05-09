package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

import javax.annotation.Nullable;

public class WoodSegmentEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(WoodSegmentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PARENT = SynchedEntityData.defineId(WoodSegmentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Vector3f> DATA_OFFSET = SynchedEntityData.defineId(WoodSegmentEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(WoodSegmentEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(WoodSegmentEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_INDEX = SynchedEntityData.defineId(WoodSegmentEntity.class, EntityDataSerializers.INT);

    public WoodSegmentEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public WoodSegmentEntity(WoodSegmentEntity segment, float yawOffset, float pitchOffset) {
        this(segment, 0.0D, 0.75D * segment.getBbHeight(), 0.0D, yawOffset, pitchOffset);
    }

    public WoodSegmentEntity(WoodSegmentEntity segment, double offsetX, double offsetY, double offsetZ, float yawOffset, float pitchOffset) {
        this(JJKEntities.WOOD_SEGMENT.get(), segment.level());

        Vec2 rot = segment.getRotationOffset();
        Vec3 offset = new Vec3(offsetX, offsetY, offsetZ).xRot(-rot.x * 0.017453292F)
                .yRot(-rot.y * 0.017453292F).add(segment.getPositionOffset());
        this.setOffset(offset.x, offset.y, offset.z, rot.y + yawOffset, rot.x + pitchOffset);
        this.setParent(segment.getParent());
        this.setPositionAndRotationFromParent();
        this.setIndex(segment.getIndex() + 1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        pBuilder.define(DATA_TIME, 0);
        pBuilder.define(DATA_PARENT, -1);
        pBuilder.define(DATA_OFFSET, Vec3.ZERO.toVector3f());
        pBuilder.define(DATA_YAW, 0.0F);
        pBuilder.define(DATA_PITCH, 0.0F);
        pBuilder.define(DATA_INDEX, 0);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public void tick() {
        super.tick();

        this.setTime(this.getTime() + 1);
    }

    protected void setOffset(double x, double y, double z, float yaw, float pitch) {
        this.entityData.set(DATA_OFFSET, new Vec3(x, y, z).toVector3f());
        this.entityData.set(DATA_YAW, yaw);
        this.entityData.set(DATA_PITCH, pitch);
    }

    protected Vec3 getPositionOffset() {
        return new Vec3(this.entityData.get(DATA_OFFSET));
    }

    protected Vec2 getRotationOffset() {
        return new Vec2(this.entityData.get(DATA_PITCH), this.entityData.get(DATA_YAW));
    }

    protected void setParent(@Nullable Entity entity) {
        this.entityData.set(DATA_PARENT, entity == null ? -1 : entity.getId());
    }

    @Nullable
    protected Entity getParent() {
        return this.level().getEntity(this.entityData.get(DATA_PARENT));
    }

    protected void setIndex(int i) {
        this.entityData.set(DATA_INDEX, i);
    }

    public int getIndex() {
        return this.entityData.get(DATA_INDEX);
    }

    protected void setPositionAndRotationFromParent() {
        Entity parent = this.getParent();

        if (parent != null) {
            float yaw = parent.yRotO + Mth.wrapDegrees(parent.getYRot() - parent.yRotO);
            double x = parent.xOld + (parent.getX() - parent.xOld);
            double y = parent.yOld + (parent.getY() - parent.yOld);
            double z = parent.zOld + (parent.getZ() - parent.zOld);
            Vec3 pos = this.getPositionOffset().yRot(-yaw * 0.017453292F).add(x, y, z);
            Vec2 rot = this.getRotationOffset();
            this.setYRot(yaw + rot.y);
            this.setXRot(rot.x);
            this.setPos(pos);
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        pCompound.putInt("time", this.getTime());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        this.setTime(pCompound.getInt("time"));
    }
}
