package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class HollowPurpleProjectile extends JujutsuProjectile {
    private static final int DELAY = 2 * 20;
    private static final float SPEED = 5.0F;
    private static final int DURATION = 5 * 20;
    private static final float DAMAGE = 30.0F;
    private static final float RADIUS = 1.0F;

    public HollowPurpleProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HollowPurpleProjectile(LivingEntity owner, float power) {
        super(JJKEntities.HOLLOW_PURPLE.get(), owner.level(), owner, power);

        Vec3 look = owner.getLookAngle();
        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
    }

    public float getRadius() {
        return RADIUS * this.getPower();
    }

    private void hurtEntities() {
        AABB bounds = this.getBoundingBox();

        if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : HelperMethods.getEntityCollisions(this.level(), bounds)) {
                if (!(entity instanceof LivingEntity living) || !owner.canAttack(living) || entity == owner) continue;
                entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.HOLLOW_PURPLE.get()),
                        DAMAGE * this.getPower());
            }
        }
    }

    private void breakBlocks() {
        for (int i = 0; i < SPEED; i++) {
            double radius = Math.max(Math.PI, this.getRadius());
            AABB bounds = this.getBoundingBox().inflate(radius);
            double centerX = bounds.getCenter().x();
            double centerY = bounds.getCenter().y();
            double centerZ = bounds.getCenter().z();

            for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
                for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                    for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = this.level().getBlockState(pos);

                        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                        if (distance <= radius) {
                            if (state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                                if (state.getFluidState().isEmpty()) {
                                    this.level().destroyBlock(pos, false);
                                } else {
                                    this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onInsideBlock(@NotNull BlockState pState) {
        if (this.getTime() >= DELAY && pState.getBlock().defaultDestroyTime() <= Block.INDESTRUCTIBLE) {
            this.discard();
        }
    }

    private void spawnParticles() {
        Vec3 center = new Vec3(this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ());
        this.level().addParticle(ParticleTypes.EXPLOSION, center.x(), center.y(), center.z(), 1.0D, 0.0D, 0.0D);
        this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, center.x(), center.y(), center.z(), 1.0D, 0.0D, 0.0D);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return EntityDimensions.fixed(this.getRadius(), this.getRadius());
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() >= DURATION) {
            this.discard();
        } else {
            if (this.getOwner() instanceof LivingEntity owner) {
                if (this.getTime() < DELAY) {
                    if (!owner.isAlive()) {
                        this.discard();
                    } else {
                        if (this.getTime() % 5 == 0) {
                            owner.swing(InteractionHand.MAIN_HAND);
                        }
                        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                                .add(owner.getLookAngle());
                        this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
                    }
                } else {
                    this.spawnParticles();

                    if (!this.level().isClientSide) {
                        this.hurtEntities();

                        if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                            this.breakBlocks();
                        }
                    }

                    if (this.getTime() == DELAY) {
                        this.setDeltaMovement(this.getLookAngle().scale(SPEED));
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);
                    }
                }
            }
        }
    }
}