package radon.jujutsu_kaisen.ability.ai.max_elephant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Water extends Ability implements Ability.IChannelened, Ability.IDurationable {
    public static final double RANGE = 20;
    private static final float SCALE = 2.0F;
    private static final float DAMAGE = 1.0F;

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return JJKAbilities.isChanneling(owner, this) || target != null && owner.distanceTo(target) <= RANGE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    private Vec3 getCollision(LivingEntity owner, Vec3 from, Vec3 to) {
        BlockHitResult result = owner.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));

        double collidePosX;
        double collidePosY;
        double collidePosZ;

        if (result.getType() != HitResult.Type.MISS) {
            Vec3 pos = result.getLocation();
            collidePosX = pos.x();
            collidePosY = pos.y();
            collidePosZ = pos.z();
        } else {
            collidePosX = to.x();
            collidePosY = to.y();
            collidePosZ = to.z();
        }
        return new Vec3(collidePosX, collidePosY, collidePosZ);
    }

    public List<Entity> checkCollisions(LivingEntity owner, Vec3 from, Vec3 to, Vec3 collision) {
        List<Entity> entities = new ArrayList<>();

        AABB bounds = new AABB(Math.min(from.x(), collision.x()), Math.min(from.y(), collision.y()),
                Math.min(from.z(), collision.z()), Math.max(from.x(), collision.x()),
                Math.max(from.y(), collision.y()), Math.max(from.z(), collision.z()))
                .inflate(SCALE);

        for (Entity entity : owner.level().getEntities(owner, bounds)) {
            float pad = entity.getPickRadius() + 0.5F;
            AABB padded = entity.getBoundingBox().inflate(pad, pad, pad);
            Optional<Vec3> hit = padded.clip(from, to);

            if (padded.contains(from)) {
                entities.add(entity);
            } else if (hit.isPresent()) {
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        Vec3 look = owner.getLookAngle();
        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY(), owner.getZ()).add(look);

        ParticleOptions particle = new VaporParticle.VaporParticleOptions(Vec3.fromRGB24(MapColor.WATER.col).toVector3f(), HelperMethods.RANDOM.nextFloat() * 5.0F,
                0.5F, false, HelperMethods.RANDOM.nextInt(20) + 1);

        for (int i = 0; i < 32; i++) {
            Vec3 dir = owner.getLookAngle().scale(3.0D);
            double dx = dir.x() + ((HelperMethods.RANDOM.nextDouble() - 0.5D) * 0.5D);
            double dy = dir.y() + ((HelperMethods.RANDOM.nextDouble() - 0.5D) * 0.5D);
            double dz = dir.z() + ((HelperMethods.RANDOM.nextDouble() - 0.5D) * 0.5D);

            ((ServerLevel) owner.level()).sendParticles(particle, spawn.x() + ((HelperMethods.RANDOM.nextDouble() - 0.5D) * 1.5D),
                    spawn.y() + ((HelperMethods.RANDOM.nextDouble() - 0.5D) * 1.5D),
                    spawn.z() + ((HelperMethods.RANDOM.nextDouble() - 0.5D) * 1.5D),
                    0, dx, dy, dz, 1.0D);
        }

        float yaw = (float) ((owner.yHeadRot + 90.0F) * Math.PI / 180.0F);
        float pitch = (float) (-owner.getXRot() * Math.PI / 180.0F);

        double endPosX = spawn.x() + RANGE * Math.cos(yaw) * Math.cos(pitch);
        double endPosY = spawn.y() + RANGE * Math.sin(pitch);
        double endPosZ = spawn.z() + RANGE * Math.sin(yaw) * Math.cos(pitch);

        Vec3 end = new Vec3(endPosX, endPosY, endPosZ);
        Vec3 collision = this.getCollision(owner, spawn, end);
        List<Entity> entities = this.checkCollisions(owner, spawn, end, collision);

        for (Entity entity : entities) {
            if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;

            entity.setDeltaMovement(spawn.subtract(entity.position()).normalize().reverse());
            entity.hurtMarked = true;

            entity.hurt(JJKDamageSources.indirectJujutsuAttack(owner, null, this), DAMAGE * this.getPower(owner));
        }

        if (owner.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            double radius = SCALE * 2.0F;

            AABB bounds = new AABB(collision.x() - radius, collision.y() - radius, collision.z() - radius,
                    collision.x() + radius, collision.y() + radius, collision.z() + radius);
            double centerX = bounds.getCenter().x();
            double centerY = bounds.getCenter().y();
            double centerZ = bounds.getCenter().z();

            for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
                for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                    for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = owner.level().getBlockState(pos);

                        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                        if (distance <= radius) {
                            if (state.getFluidState().isEmpty() && !state.canOcclude()) {
                                owner.level().destroyBlock(pos, false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 5.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public int getDuration() {
        return 3 * 20;
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {

    }
}
