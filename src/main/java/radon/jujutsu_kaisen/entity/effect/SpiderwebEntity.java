package radon.jujutsu_kaisen.entity.effect;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.shrine.Spiderweb;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.projectile.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SpiderwebEntity extends JujutsuProjectile {
    private static final float RADIUS = 4.0F;
    private static final float MAX_RADIUS = 8.0F;

    private static final EntityDataAccessor<BlockPos> DATA_CENTER = SynchedEntityData.defineId(SpiderwebEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> DATA_FACE = SynchedEntityData.defineId(SpiderwebEntity.class, EntityDataSerializers.INT);

    private int charge;

    public SpiderwebEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public SpiderwebEntity(LivingEntity owner, float power, BlockPos pos, Direction face) {
        super(JJKEntities.SPIDERWEB.get(), owner.level(), owner, power);

        this.noPhysics = true;

        this.entityData.set(DATA_CENTER, pos);
        this.entityData.set(DATA_FACE, face.ordinal());

        this.update();
    }

    private BlockPos getCenter() {
        return this.entityData.get(DATA_CENTER);
    }

    private Direction getFace() {
        return Direction.values()[this.entityData.get(DATA_FACE)];
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_CENTER, BlockPos.ZERO);
        pBuilder.define(DATA_FACE, -1);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.put("center", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE,
                this.getCenter()).getOrThrow());
        pCompound.putInt("face", this.entityData.get(DATA_FACE));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_CENTER, BlockPos.CODEC.parse(NbtOps.INSTANCE,
                pCompound.get("center")).getOrThrow());

        this.entityData.set(DATA_FACE, pCompound.getInt("face"));
    }

    private void update() {
        BlockPos center = this.getCenter();
        Direction face = this.getFace();

        Vec3 pos = center.relative(face).getCenter();
        pos = pos.subtract(face.getStepX() * 0.5D, face.getStepY() * 0.5D, face.getStepZ() * 0.5D);

        float xRot = (float) (Mth.atan2(face.getStepY(), face.getStepX()) * 180.0F / Mth.PI);

        switch (face) {
            case UP, DOWN -> xRot = -xRot;
            case WEST -> xRot -= 180.0F;
        }

        float radius = this.getScaledRadius();
        Direction opposite = face.getOpposite();
        pos = pos.add(opposite.getStepX() * radius,
                opposite.getStepY() * radius,
                opposite.getStepZ() * radius);

        this.moveTo(pos.x, pos.y - radius, pos.z, face.toYRot(), xRot);
    }

    public float getMaximumRadius() {
        return Math.min(MAX_RADIUS, RADIUS * this.getPower());
    }

    public float getScaledRadius() {
        float radius = this.getMaximumRadius();
        float scale = (float) Math.pow((double) Math.min(Spiderweb.MAX_CHARGE, this.charge) / Spiderweb.MAX_CHARGE, 0.5D);
        return radius * scale;
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float radius = this.getScaledRadius() * 2;
        return EntityDimensions.fixed(radius, radius);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.refreshDimensions();
    }

    @Override
    public void tick() {
        super.tick();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        if (!data.isChanneling(JJKAbilities.SPIDERWEB.get())) {
            this.explode();
            this.discard();
            return;
        }

        this.charge = data.getCharge();

        int time = this.getTime();

        if (time < Spiderweb.MAX_CHARGE && time % 2 == 0) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }

        this.refreshDimensions();

        this.update();
    }

    private void explode() {
        if (!(this.level() instanceof ServerLevel level)) return;
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        float radius = this.getScaledRadius();
        float diameter = radius * 2;

        Vec3 look = this.getLookAngle();
        Vec3 direction = look.scale(radius);
        Vec3 center = this.position()
                .add(0.0D, radius, 0.0D)
                .add(direction);

        int size = (int) Math.ceil(radius);
        int cx = Mth.floor(center.x);
        int cy = Mth.floor(center.y);
        int cz = Mth.floor(center.z);

        for (int x = cx - size; x <= cx + size; x++) {
            for (int y = cy - size; y <= cy + size; y++) {
                for (int z = cz - size; z <= cz + size; z++) {
                    Vec3 blockCenter = new Vec3(x + 0.5, y + 0.5, z + 0.5);
                    Vec3 offset = blockCenter.subtract(center);

                    if (offset.dot(look) >= 0) continue;

                    if (offset.length() > radius) continue;

                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (!HelperMethods.isDestroyable(level, this, owner, pos)) continue;

                    if (state.getFluidState().isEmpty()) {
                        level.destroyBlock(pos, false);
                    } else {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(),
                                Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                    }
                }
            }
        }

        level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, diameter, 1.0F);
    }

    @Override
    protected boolean isProjectile() {
        return false;
    }
}