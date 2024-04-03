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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.*;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class HollowPurpleProjectile extends JujutsuProjectile {
    private static final int DELAY = 2 * 20;
    private static final float SPEED = 5.0F;
    private static final int DURATION = 5 * 20;
    private static final float DAMAGE = 30.0F;
    private static final float MAX_RADIUS = 6.0F;
    private static final float RADIUS = 2.0F;
    private static final int ANIMATION = 20;

    public HollowPurpleProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public HollowPurpleProjectile(LivingEntity owner, float power) {
        super(JJKEntities.HOLLOW_PURPLE.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(look.scale(this.getRadius())));
    }

    public float getRadius() {
        return Math.max(Mth.PI, Math.min(MAX_RADIUS, RADIUS * this.getPower()));
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
                                this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return EntityDimensions.fixed(this.getRadius(), this.getRadius());
    }

    private void renderBlue(Vec3 center) {
        float radius = this.getRadius();
        int count = (int) (radius * Math.PI * 2);

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.75F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.75F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.75F * Math.cos(phi);

            double x = center.x + xOffset * (radius * 0.1F);
            double y = center.y + yOffset * (radius * 0.1F);
            double z = center.z + zOffset * (radius * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.DARK_BLUE, radius * 0.2F, 1.0F, true, 5), true,
                    center.x, center.y, center.z, 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.5F * Math.cos(phi);

            double x = center.x + xOffset * 0.1F;
            double y = center.y + yOffset * 0.1F;
            double z = center.z + zOffset * 0.1F;

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_BLUE, radius * 0.1F, 1.0F, true, 5), true,
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    private void renderRed(Vec3 center) {
        float radius = this.getRadius();
        int count = (int) (radius * Math.PI * 2);

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.75F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.75F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.75F * Math.cos(phi);

            double x = center.x + xOffset * (radius * 0.1F);
            double y = center.y + yOffset * (radius * 0.1F);
            double z = center.z + zOffset * (radius * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.DARK_RED, radius * 0.2F, 1.0F, true, 5), true,
                    center.x, center.y, center.z, 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.5F * Math.cos(phi);

            double x = center.x + xOffset * 0.1F;
            double y = center.y + yOffset * 0.1F;
            double z = center.z + zOffset * 0.1F;

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_RED, radius * 0.1F, 1.0F, true, 5), true,
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }
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

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        this.renderRed(center.add(pos));
        this.renderBlue(center.subtract(pos));
    }

    private void spawnParticles() {
        if (this.getTime() <= ANIMATION) {
            this.animate();
            return;
        }

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        float radius = this.getRadius() * 2.0F;
        int count = (int) (radius * Math.PI * 2);

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.75F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.75F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.75F * Math.cos(phi);

            double x = center.x + xOffset * (radius * 0.1F);
            double y = center.y + yOffset * (radius * 0.1F);
            double z = center.z + zOffset * (radius * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.DARK_PURPLE, radius * 0.2F, 1.0F, true, 5), true,
                    center.x, center.y, center.z, 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.75F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.75F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.75F * Math.cos(phi);

            double x = center.x + xOffset * 0.1F;
            double y = center.y + yOffset * 0.1F;
            double z = center.z + zOffset * 0.1F;

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_PURPLE, radius * 0.2F, 1.0F, true, 5), true,
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < 4; i++) {
            this.level().addParticle(new EmittingLightningParticle.EmittingLightningParticleOptions(ParticleColors.LIGHT_PURPLE, radius * 1.25F, 1),
                    true, this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
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
                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                        .add(look.scale(this.getRadius())));
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