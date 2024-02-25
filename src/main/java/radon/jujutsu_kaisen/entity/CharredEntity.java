package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class CharredEntity extends Entity {
    public static final int DURATION = 30 * 20;

    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(CharredEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_ENTITY = SynchedEntityData.defineId(CharredEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<CompoundTag> DATA_TAG = SynchedEntityData.defineId(CharredEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Float> DATA_WIDTH = SynchedEntityData.defineId(CharredEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT = SynchedEntityData.defineId(CharredEntity.class, EntityDataSerializers.FLOAT);

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    public CharredEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public CharredEntity(LivingEntity entity) {
        this(JJKEntities.CHARRED.get(), entity.level());

        this.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());

        this.setEntity(entity.getType());

        CompoundTag tag = new CompoundTag();

        if (!(entity instanceof Player)) {
            entity.saveWithoutId(tag);
        }
        this.setTag(tag);

        this.setWidth(entity.getBbWidth());
        this.setHeight(entity.getBbHeight());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TIME, 0);
        this.entityData.define(DATA_ENTITY, "");
        this.entityData.define(DATA_TAG, new CompoundTag());
        this.entityData.define(DATA_WIDTH, 0.0F);
        this.entityData.define(DATA_HEIGHT, 0.0F);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    public EntityType<?> getEntity() {
        return EntityType.byString(this.entityData.get(DATA_ENTITY)).orElseThrow();
    }

    public void setEntity(EntityType<?> type) {
        this.entityData.set(DATA_ENTITY, BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
    }

    public CompoundTag getTag() {
        return this.entityData.get(DATA_TAG);
    }

    public void setTag(CompoundTag tag) {
        this.entityData.set(DATA_TAG, tag);
    }

    private float getWidth() {
        return this.entityData.get(DATA_WIDTH);
    }

    private void setWidth(float size) {
        this.entityData.set(DATA_WIDTH, size);
    }

    private float getHeight() {
        return this.entityData.get(DATA_HEIGHT);
    }

    private void setHeight(float size) {
        this.entityData.set(DATA_HEIGHT, size);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        this.setTime(pCompound.getInt("time"));
        this.setEntity(EntityType.byString(pCompound.getString("type")).orElseThrow());
        this.setWidth(pCompound.getFloat("width"));
        this.setHeight(pCompound.getFloat("height"));

        if (pCompound.contains("tag")) {
            this.setTag(pCompound.getCompound("tag"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        pCompound.putInt("time", this.getTime());
        pCompound.putString("type", this.getEntity().toString());
        pCompound.putFloat("width", this.getWidth());
        pCompound.putFloat("height", this.getHeight());
        pCompound.put("tag", this.getTag());
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return EntityDimensions.fixed(this.getWidth(), this.getHeight());
    }

    private float getFrictionInfluencedSpeed(float pFriction) {
        return this.onGround() ? 0.7F * (0.21600002F / (pFriction * pFriction * pFriction)) : 0.02F;
    }

    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 pDeltaMovement, float pFriction) {
        this.moveRelative(this.getFrictionInfluencedSpeed(pFriction), pDeltaMovement);
        this.move(MoverType.SELF, this.getDeltaMovement());
        return this.getDeltaMovement();
    }

    private void doPush(Entity entity) {
        entity.push(this);
    }

    private void pushEntities() {
        AABB bounds = this.getBoundingBox();

        if (this.level().isClientSide) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), bounds, EntitySelector.pushableBy(this)).forEach(this::doPush);
        } else {
            List<Entity> entities = this.level().getEntities(this, bounds, EntitySelector.pushableBy(this));

            if (!entities.isEmpty()) {
                int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);

                if (i > 0 && entities.size() > i - 1 && this.random.nextInt(4) == 0) {
                    int j = 0;

                    for (Entity entity : entities) {
                        if (!entity.isPassenger()) {
                            ++j;
                        }
                    }

                    if (j > i - 1) {
                        this.hurt(this.damageSources().cramming(), 6.0F);
                    }
                }

                for (Entity entity : entities) {
                    this.doPush(entity);
                }
            }
        }
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements) {
        this.lerpX = pX;
        this.lerpY = pY;
        this.lerpZ = pZ;
        this.lerpYRot = pYaw;
        this.lerpXRot = pPitch;
        this.lerpSteps = pPosRotationIncrements;
    }

    public void travel() {
        if (this.isControlledByLocalInstance()) {
            double d0 = 0.08D;

            BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement();
            float f2 = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getFriction(level(), this.getBlockPosBelowThatAffectsMyMovement(), this);
            float f3 = this.onGround() ? f2 * 0.91F : 0.91F;
            Vec3 vec35 = this.handleRelativeFrictionAndCalculateMovement(Vec3.ZERO, f2);

            double d2 = vec35.y;

            if (this.level().isClientSide && !this.level().hasChunkAt(pos)) {
                if (this.getY() > (double) this.level().getMinBuildHeight()) {
                    d2 = -0.1D;
                } else {
                    d2 = 0.0D;
                }
            } else if (!this.isNoGravity()) {
                d2 -= d0;
            }
            this.setDeltaMovement(vec35.x * (double) f3, d2 * (double) 0.98F, vec35.z * (double) f3);
        }
    }

    public void aiStep() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d2 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d4 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d6 = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            this.setYRot(this.getYRot() + (float) d6 / (float) this.lerpSteps);
            this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d2, d4);
            this.setRot(this.getYRot(), this.getXRot());
        } else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }

        Vec3 vec31 = this.getDeltaMovement();
        double d1 = vec31.x;
        double d3 = vec31.y;
        double d5 = vec31.z;

        if (Math.abs(vec31.x) < 0.003D) {
            d1 = 0.0D;
        }
        if (Math.abs(vec31.y) < 0.003D) {
            d3 = 0.0D;
        }
        if (Math.abs(vec31.z) < 0.003D) {
            d5 = 0.0D;
        }
        this.setDeltaMovement(d1, d3, d5);
        this.travel();
        this.pushEntities();
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        if (this.getTime() >= DURATION) {
            this.discard();
            return;
        }

        super.tick();

        this.refreshDimensions();

        double x = this.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (this.getBbWidth() * 1.5F);
        double y = this.getY() + HelperMethods.RANDOM.nextDouble() * this.getBbHeight();
        double z = this.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (this.getBbWidth() * 1.5F);
        double speed = (this.getBbHeight() * 0.1F) * HelperMethods.RANDOM.nextDouble();
        this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0.0D, speed, 0.0D);

        this.aiStep();
    }
}
