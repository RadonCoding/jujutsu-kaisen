package radon.jujutsu_kaisen.entity.projectile;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ParticleAnimator;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class HollowPurpleProjectile extends JujutsuProjectile {
    private static final int DELAY = 2 * 20;
    private static final float SPEED = 5.0F;
    private static final int DURATION = 5 * 20;
    private static final float DAMAGE = 30.0F;
    private static final float MAX_RADIUS = 10.0F;
    private static final float RADIUS = 4.0F;
    private static final int ANIMATION = 20;

    public HollowPurpleProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public HollowPurpleProjectile(LivingEntity owner, float power) {
        super(JJKEntities.HOLLOW_PURPLE.get(), owner.level(), owner, power);

        float radius = this.getRadius() * 0.5F;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2), owner.getZ())
                .add(look.scale(radius)));
    }

    public float getRadius() {
        return Math.max(RADIUS, Math.min(MAX_RADIUS, RADIUS * this.getPower()));
    }

    private void hurtEntities() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        double radius = this.getRadius();
        AABB bounds = this.getBoundingBox().inflate(radius);

        for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, owner.level(), owner, bounds)) {
            if (Math.sqrt(entity.distanceToSqr(bounds.getCenter())) > radius) continue;

            if (entity == this) continue;

            if (entity instanceof Projectile) {
                entity.discard();
                continue;
            }
            entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.HOLLOW_PURPLE.get()),
                    DAMAGE * this.getPower());
        }
    }

    private void breakBlocks() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        for (int i = 0; i < SPEED; i++) {
            double radius = this.getRadius();
            AABB bounds = this.getBoundingBox().inflate(radius);

            double centerX = bounds.getCenter().x;
            double centerY = bounds.getCenter().y;
            double centerZ = bounds.getCenter().z;

            for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
                for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                    for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = this.level().getBlockState(pos);

                        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                        if (distance > radius) continue;

                        if (HelperMethods.isDestroyable((ServerLevel) this.level(), this, owner, pos)) {
                            if (state.getFluidState().isEmpty()) {
                                this.level().destroyBlock(pos, false);
                            } else {
                                this.level().setBlock(pos, Blocks.AIR.defaultBlockState(),
                                        Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float radius = this.getRadius();
        return EntityDimensions.fixed(radius, radius);
    }

    private void renderBlue(Vec3 center) {
        float radius = this.getRadius();
        int count = Math.round(radius * 2.5F);

        ParticleAnimator.sphere(this.level(), center, () -> radius * 0.1F, () -> radius * 0.01F,
                () -> radius * 0.1F, count, 0.5F, true, true, 5, ParticleColors.LIGHT_BLUE);
    }

    private void renderRed(Vec3 center) {
        float radius = this.getRadius();
        int count = Math.round(radius * 2.5F);

        ParticleAnimator.sphere(this.level(), center, () -> radius * 0.1F, () -> radius * 0.01F,
                () -> radius * 0.1F, count, 0.5F, true, true, 5, ParticleColors.DARK_RED);
    }

    private void animate() {
        float size = this.getRadius() / Mth.PI;
        float fraction = (float) this.getTime() / ANIMATION;
        fraction = fraction < 0.5F ? 2 * fraction * fraction : fraction;
        float offset = Mth.lerp(fraction, size * 2, 0.0F);

        Entity owner = this.getOwner();

        if (owner == null) return;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        float yaw = owner.getYRot();
        Vec3 right = new Vec3(-Math.sin(Math.toRadians(yaw)), 0.0D, Math.cos(Math.toRadians(yaw)));
        Vec3 pos = look.cross(right).normalize().scale(offset);

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2), this.getZ());

        this.renderRed(center.add(pos));
        this.renderBlue(center.subtract(pos));
    }

    private void spawnParticles() {
        if (this.getTime() <= ANIMATION) {
            this.animate();
            return;
        }

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2), this.getZ());

        float radius = this.getRadius();
        int count = Math.round(radius * 5.0F);

        ParticleAnimator.sphere(this.level(), center, () -> radius * 0.5F, () -> 0.0F,
                () -> radius * 0.25F, count, 1.0F, true, true, 5, ParticleColors.DARK_PURPLE);

        ParticleAnimator.sphere(this.level(), center, () -> radius * 0.35F, () -> 0.0F,
                () -> radius * 0.25F, count, 1.0F, true, true, 5, ParticleColors.LIGHT_PURPLE);

        ParticleAnimator.lightning(this.level(), center, radius * 0.25F, () -> radius,
                count / 4, 8, ParticleColors.LIGHT_PURPLE);
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() >= DURATION) {
            this.discard();
            return;
        }

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        this.spawnParticles();

        if (this.getTime() < DELAY) {
            if (!owner.isAlive()) {
                this.discard();
            } else {
                if (this.getTime() % 5 == 0) {
                    owner.swing(InteractionHand.MAIN_HAND);
                }

                float radius = this.getRadius() * 0.5F;

                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2), owner.getZ())
                        .add(look.scale(radius)));
            }
        } else {
            if (!this.level().isClientSide) {
                this.hurtEntities();
                this.breakBlocks();
            }

            if (this.getTime() == DELAY) {
                this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);
            }
        }
    }
}