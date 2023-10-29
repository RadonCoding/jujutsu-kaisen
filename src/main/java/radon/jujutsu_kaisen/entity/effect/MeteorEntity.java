package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;

import java.util.List;

public class MeteorEntity extends JujutsuProjectile {
    public static final int SIZE = 5;
    public static final int HEIGHT = 30;
    private static final int MAX_SIZE = 20;

    private static final float DAMAGE = 40.0F;
    private static final int EXPLOSION_DURATION = (SIZE / 2) * 20;
    private static final int MAXIMUM_TIME = EXPLOSION_DURATION / 4;

    private int explosionTime;

    private float xxa;
    private float zza;

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    public MeteorEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MeteorEntity(LivingEntity owner, float power) {
        super(JJKEntities.METEOR.get(), owner.level(), owner, power);

        this.setPos(owner.position().add(0.0D, HEIGHT, 0.0D));
        owner.startRiding(this);
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight();
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.explosionTime = pCompound.getInt("explosion_time");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("explosion_time", this.explosionTime);
    }

    private float getFrictionInfluencedSpeed(float pFriction) {
        return this.onGround() ? 0.7F * (0.21600002F / (pFriction * pFriction * pFriction)) : 0.02F;
    }

    private Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 pDeltaMovement, float pFriction) {
        this.moveRelative(this.getFrictionInfluencedSpeed(pFriction), pDeltaMovement);
        this.move(MoverType.SELF, this.getDeltaMovement());
        Vec3 vec3 = this.getDeltaMovement();

        if (this.horizontalCollision) {
            vec3 = new Vec3(vec3.x, 0.2D, vec3.z);
        }
        return vec3;
    }

    private void travel(Vec3 pTravelVector) {
        if (!this.level().isClientSide) {
            double d0 = 0.08D;

            BlockPos blockpos = this.getBlockPosBelowThatAffectsMyMovement();
            float f2 = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getFriction(this.level(), this.getBlockPosBelowThatAffectsMyMovement(), this);
            float f3 = this.onGround() ? f2 * 0.91F : 0.91F;
            Vec3 vec35 = this.handleRelativeFrictionAndCalculateMovement(pTravelVector, f2);
            double d2 = vec35.y;

            if (this.level().isClientSide && !this.level().hasChunkAt(blockpos)) {
                if (this.getY() > (double)this.level().getMinBuildHeight()) {
                    d2 = -0.1D;
                } else {
                    d2 = 0.0D;
                }
            } else if (!this.isNoGravity()) {
                d2 -= d0;
            }
            this.setDeltaMovement(vec35.x * (double)f3, d2 * (double)0.98F, vec35.z * (double)f3);
        }
    }

    private void doPush(Entity entity) {
        entity.push(this);
    }

    private void pushEntities() {
        if (this.level().isClientSide) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::doPush);
        } else {
            List<Entity> entities = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));

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
    public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements, boolean pTeleport) {
        this.lerpX = pX;
        this.lerpY = pY;
        this.lerpZ = pZ;
        this.lerpYRot = pYaw;
        this.lerpXRot = pPitch;
        this.lerpSteps = pPosRotationIncrements;
    }

    public void aiStep() {
        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d2 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d4 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double d6 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
            this.setYRot(this.getYRot() + (float)d6 / (float)this.lerpSteps);
            this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d2, d4);
            this.setRot(this.getYRot(), this.getXRot());
        } else if (!this.level().isClientSide) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }

        Vec3 vec3 = this.getDeltaMovement();
        double d1 = vec3.x;
        double d3 = vec3.y;
        double d5 = vec3.z;
        if (Math.abs(vec3.x) < 0.003D) {
            d1 = 0.0D;
        }

        if (Math.abs(vec3.y) < 0.003D) {
            d3 = 0.0D;
        }

        if (Math.abs(vec3.z) < 0.003D) {
            d5 = 0.0D;
        }

        this.setDeltaMovement(d1, d3, d5);

        this.xxa *= 0.98F;
        this.zza *= 0.98F;
        this.travel(new Vec3(this.xxa, 0.0D, this.zza));
        this.pushEntities();
    }

    public int getSize() {
        return Math.min(MAX_SIZE, Math.round(SIZE * this.getPower()));
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        int size = this.getSize();
        return EntityDimensions.fixed(size * 2, size * 2);
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (!this.isRemoved()) {
            this.aiStep();
        }

        if (!this.level().isClientSide) {
            if (this.getOwner() instanceof LivingEntity owner) {
                if (this.explosionTime == 0) {
                    for (Entity entity : this.level().getEntities(owner, this.getBoundingBox().expandTowards(0.0D, (double) -this.getSize() / 2, 0.0D))) {
                        entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.MAXIMUM_METEOR.get()), DAMAGE * this.getPower());
                    }
                }

                if (this.onGround()) {
                    if (this.explosionTime == 0) {
                        ExplosionHandler.spawn(this.level().dimension(), this.blockPosition(), this.getSize() * 1.5F,
                                EXPLOSION_DURATION, owner, JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.MAXIMUM_METEOR.get()));
                        this.explosionTime++;
                    }
                }

                if (this.explosionTime > 0) {
                    if (this.explosionTime >= MAXIMUM_TIME) {
                        this.discard();
                    } else {
                        if (this.explosionTime < MAXIMUM_TIME / 4) {
                            BlockPos.betweenClosedStream(this.getBoundingBox().inflate(1.0D)).forEach(pos -> {
                                BlockState state = this.level().getBlockState(pos);

                                if (state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE && !state.isAir()) {
                                    this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                                }
                            });
                        }
                        this.explosionTime++;
                    }
                }
            }
        }
    }
}
