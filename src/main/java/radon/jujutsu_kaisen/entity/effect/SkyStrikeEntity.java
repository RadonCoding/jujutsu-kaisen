package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;

import java.util.List;

public class SkyStrikeEntity extends JujutsuProjectile {
    private static final float DAMAGE = 15.0F;

    public static final int STRIKE_EXPLOSION = 17;
    private static final int STRIKE_LENGTH = 21;

    private int strikeTimeO;
    private int strikeTime;

    public SkyStrikeEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public SkyStrikeEntity(LivingEntity owner, float power, Vec3 pos) {
        this(JJKEntities.SKY_STRIKE.get(), owner.level());

        this.setOwner(owner);
        this.setPower(power);

        this.setPos(pos.x, pos.y + 1.0625F, pos.z);
    }

    public float getStrikeDrawTime(float partialTicks) {
        return this.getActualStrikeTime(partialTicks) / STRIKE_EXPLOSION;
    }

    public float getStrikeDamageTime(float partialTicks) {
        return (this.getActualStrikeTime(partialTicks) - STRIKE_EXPLOSION) / (STRIKE_LENGTH - STRIKE_EXPLOSION);
    }

    public boolean isStrikeDrawing(float partialTicks) {
        return this.getActualStrikeTime(partialTicks) < STRIKE_EXPLOSION;
    }

    public boolean isStriking(float partialTicks) {
        return this.getActualStrikeTime(partialTicks) < STRIKE_LENGTH;
    }

    private float getActualStrikeTime(float delta) {
        return this.strikeTimeO + (this.strikeTime - this.strikeTimeO) * delta;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0D;
    }

    @Override
    public void tick() {
        super.tick();

        this.strikeTimeO = this.strikeTime;

        if (this.strikeTime == 0) {
            this.playSound(SoundEvents.WARDEN_SONIC_CHARGE);
        }

        this.moveDownToGround();

        if (this.strikeTime >= STRIKE_LENGTH || !this.level().canSeeSkyFromBelowWater(this.blockPosition())) {
            this.discard();
        } else if (this.strikeTime == STRIKE_EXPLOSION) {
            this.playSound(SoundEvents.WARDEN_SONIC_BOOM);

            this.hurtEntities(3);
        }
        this.strikeTime++;
    }

    public void moveDownToGround() {
        HitResult hit = this.getHitResult();

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;

            if (blockHit.getDirection() == Direction.UP) {
                BlockState state = this.level().getBlockState(blockHit.getBlockPos());

                if (this.strikeTime > STRIKE_LENGTH && state != this.level().getBlockState(blockPosition().below())) {
                    this.discard();
                }
                if (state.getBlock() instanceof SlabBlock && state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
                    this.setPos(getX(), blockHit.getBlockPos().getY() + 1.0625F - 0.5F, getZ());
                } else {
                    this.setPos(getX(), blockHit.getBlockPos().getY() + 1.0625F, getZ());
                }
                if (this.level() instanceof ServerLevel) {
                    ((ServerLevel) this.level()).getChunkSource().broadcast(this, new ClientboundTeleportEntityPacket(this));
                }
            }
        }
    }

    public void hurtEntities(double radius) {
        AABB bounds = new AABB(this.getX() - radius, this.getY() - 0.5D, this.getZ() - radius, this.getX() + radius, Double.POSITIVE_INFINITY, this.getZ() + radius);
        List<Entity> entities = this.level().getEntities(this, bounds);
        double radiusSq = radius * radius;

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        for (Entity entity : entities) {
            if (entity == owner) continue;

            if (this.getDistanceSqXZToEntity(entity) < radiusSq) {
                entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.SKY_STRIKE.get()), DAMAGE * this.getPower());
            }
        }
    }

    public double getDistanceSqXZToEntity(Entity entity) {
        double d0 = this.getX() - entity.getX();
        double d2 = this.getZ() - entity.getZ();
        return d0 * d0 + d2 * d2;
    }

    private HitResult getHitResult() {
        Vec3 startPos = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 endPos = new Vec3(this.getX(), this.level().getMinBuildHeight(), this.getZ());
        return this.level().clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this));
    }
}