package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class AirFrameEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(AirFrameEntity.class, EntityDataSerializers.INT);

    private static final int DURATION = 20;
    private static final float DAMAGE = 15.0F;
    private static final double RANGE = 3.0D;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    private float power;

    public AirFrameEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public AirFrameEntity(LivingEntity owner, float power) {
        this(JJKEntities.AIR_FRAME.get(), owner.level());

        this.setOwner(owner);

        this.power = power;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TIME, 0);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putFloat("power", this.power);
        pCompound.putInt("time", this.getTime());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.power = pCompound.getFloat("power");
        this.setTime(pCompound.getInt("time"));
    }

    public float getPower() {
        return this.power;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 10.0D;

        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        d0 *= 64.0D * getViewScale();
        return pDistance < d0 * d0;
    }

    @Override
    public void tick() {
        super.tick();

        this.setTime(this.getTime() + 1);

        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || owner.isDeadOrDying())) {
            this.discard();
        } else {
            super.tick();

            if (this.level().isClientSide) return;

            if (this.getTime() >= DURATION) {
                if (owner != null) {
                    Vec3 center = this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D);
                    ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                    ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                    owner.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS,
                            4.0F, (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F);

                    for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(center, RANGE, RANGE, RANGE))) {
                        entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.AIR_FRAME.get()), DAMAGE * this.getPower());
                    }
                }
                this.discard();
            }
        }
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }
}
