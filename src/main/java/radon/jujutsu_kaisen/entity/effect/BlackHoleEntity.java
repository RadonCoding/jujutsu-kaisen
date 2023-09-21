package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BlackHoleEntity extends JujutsuProjectile {
    private static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Vector3f> DATA_COLOR = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.VECTOR3);

    private Ability ability;

    public BlackHoleEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.noCulling = true;
    }

    public BlackHoleEntity(LivingEntity pShooter, Ability ability, int duration, float size, Vector3f color) {
        this(JJKEntities.BLACK_HOLE.get(), pShooter.level);

        this.ability = ability;

        this.setOwner(pShooter);

        this.entityData.set(DATA_DURATION, duration);
        this.entityData.set(DATA_SIZE, size);
        this.entityData.set(DATA_COLOR, color);
    }

    private void pullEntities() {
        double radius = this.getSize() * 2.0F;
        AABB bounds = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius);

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : this.level.getEntities(owner, bounds)) {
                if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || (entity instanceof Projectile projectile && projectile.getOwner() == owner)) continue;

                Vec3 direction = center.subtract(entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0D), entity.getZ()).normalize();
                entity.setDeltaMovement(direction);
                entity.hurtMarked = true;
            }
        }
    }

    private void pullBlocks() {
        double radius = this.getSize() * 2.0F;
        AABB bounds = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius);
        double centerX = bounds.getCenter().x();
        double centerY = bounds.getCenter().y();
        double centerZ = bounds.getCenter().z();

        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                    if (this.random.nextInt(50) != 0) continue;

                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level.getBlockState(pos);

                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= radius) {
                        if (!state.isAir() && state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                            if (this.level.destroyBlock(pos, false)) {
                                FallingBlockEntity entity = FallingBlockEntity.fall(this.level, pos, state);

                                if (((ServerLevel) this.level).getEntity(entity.getUUID()) == null) {
                                    this.level.addFreshEntity(entity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void hurtEntities() {
        AABB bounds = this.getBoundingBox();

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (Entity entity : HelperMethods.getEntityCollisions(this.level, bounds)) {
                    if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner || entity == this) continue;

                    if (entity instanceof LivingEntity) {
                        entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, this.ability), this.getSize() * cap.getGrade().getPower(owner));
                    } else {
                        entity.discard();
                    }
                }
            });
        }
    }

    private void breakBlocks() {
        AABB bounds = this.getBoundingBox();
        double centerX = bounds.getCenter().x();
        double centerY = bounds.getCenter().y();
        double centerZ = bounds.getCenter().z();

        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level.getBlockState(pos);

                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= this.getSize()) {
                        if (!state.isAir() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                            if (state.getFluidState().isEmpty()) {
                                this.level.destroyBlock(pos, false);
                            } else {
                                this.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return EntityDimensions.fixed(this.getSize(), this.getSize());
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() >= this.getDuration()) {
            this.discard();
        } else {
            this.pullEntities();
            this.hurtEntities();

            if (!this.level.isClientSide) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    this.pullBlocks();
                    this.breakBlocks();
                }
            }
        }
    }

    public int getDuration() {
        return this.entityData.get(DATA_DURATION);
    }

    public float getSize() {
        return this.entityData.get(DATA_SIZE);
    }

    public Vector3f getColor() {
        return this.entityData.get(DATA_COLOR);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_DURATION, 0);
        this.entityData.define(DATA_SIZE, 0.0F);
        this.entityData.define(DATA_COLOR, Vec3.ZERO.toVector3f());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.ability = JJKAbilities.getValue(new ResourceLocation(pCompound.getString("ability")));

        this.entityData.set(DATA_DURATION, pCompound.getInt("duration"));
        this.entityData.set(DATA_SIZE, pCompound.getFloat("size"));
        this.entityData.set(DATA_COLOR, Vec3.fromRGB24(pCompound.getInt("color")).toVector3f());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putString("ability", JJKAbilities.getKey(this.ability).toString());

        pCompound.putInt("duration", this.entityData.get(DATA_DURATION));
        pCompound.putFloat("size", this.entityData.get(DATA_SIZE));
        pCompound.putInt("color", HelperMethods.toRGB24(this.entityData.get(DATA_COLOR)));
    }
}
