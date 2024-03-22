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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;

import java.util.UUID;

public class ProjectionFrameEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(ProjectionFrameEntity.class, EntityDataSerializers.INT);

    private static final int DURATION = 3 * 20;

    @Nullable
    private UUID victimUUID;
    @Nullable
    private LivingEntity cachedVictim;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    private Vec3 pos;
    private float power;

    public ProjectionFrameEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public ProjectionFrameEntity(LivingEntity owner, LivingEntity target, float power) {
        this(JJKEntities.PROJECTION_FRAME.get(), owner.level());

        this.setOwner(owner);
        this.setVictim(target);

        this.power = power;

        this.pos = target.position();

        this.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
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
        pCompound.putDouble("pos_x", this.pos.x);
        pCompound.putDouble("pos_y", this.pos.y);
        pCompound.putDouble("pos_z", this.pos.z);

        if (this.victimUUID != null) {
            pCompound.putUUID("victim", this.victimUUID);
        }
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putFloat("power", this.power);
        pCompound.putInt("time", this.getTime());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        this.pos = new Vec3(pCompound.getDouble("pos_x"), pCompound.getDouble("pos_y"), pCompound.getDouble("pos_z"));

        if (pCompound.hasUUID("victim")) {
            this.victimUUID = pCompound.getUUID("victim");
        }
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
        this.setTime(this.getTime() + 1);

        LivingEntity victim = this.getVictim();

        if (!this.level().isClientSide && (victim == null || victim.isRemoved() || victim.isDeadOrDying())) {
            this.discard();
        } else {
            super.tick();

            if (this.level().isClientSide) return;

            if (this.getTime() >= DURATION) {
                this.discard();
            } else if (victim != null) {
                victim.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 2, 1, false, false, false));

                if (this.pos != null) {
                    victim.teleportTo(this.pos.x, this.pos.y, this.pos.z);
                }
            }
        }
    }

    public void setVictim(@Nullable LivingEntity victim) {
        if (victim != null) {
            this.victimUUID = victim.getUUID();
            this.cachedVictim = victim;
        }
    }

    @Nullable
    public LivingEntity getVictim() {
        if (this.cachedVictim != null && !this.cachedVictim.isRemoved()) {
            return this.cachedVictim;
        } else if (this.victimUUID != null && this.level() instanceof ServerLevel) {
            this.cachedVictim = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.victimUUID);
            return this.cachedVictim;
        } else {
            return null;
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

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        LivingEntity entity = this.getVictim();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        LivingEntity victim = (LivingEntity) this.level().getEntity(pPacket.getData());

        if (victim != null) {
            this.setVictim(victim);
        }
    }
}
