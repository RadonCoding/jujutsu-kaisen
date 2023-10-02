package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class FilmGaugeProjectile extends JujutsuProjectile {
    private static final EntityDataAccessor<Vector3f> DATA_START = SynchedEntityData.defineId(FilmGaugeProjectile.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Boolean> DATA_ATTACHED = SynchedEntityData.defineId(FilmGaugeProjectile.class, EntityDataSerializers.BOOLEAN);

    private static final float SPEED = 1.0F;

    @Nullable
    private UUID targetUUID;
    @Nullable
    private LivingEntity cachedTarget;

    public FilmGaugeProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.noCulling = true;
    }

    public FilmGaugeProjectile(LivingEntity pShooter, LivingEntity target, DomainExpansionCenterEntity center) {
        this(JJKEntities.FILM_GAUGE.get(), pShooter.level());

        this.setOwner(pShooter);
        this.setTarget(target);

        Vec3 look = HelperMethods.getLookAngle(center);
        Vec3 spawn = new Vec3(center.getX(), center.getY() + (center.getBbHeight() / 2.0F) - (this.getBbHeight() / 2.0F), center.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), center.getYRot(), center.getXRot());

        this.entityData.set(DATA_START, this.position().toVector3f());
    }

    public void setTarget(@Nullable LivingEntity target) {
        if (target != null) {
            this.targetUUID = target.getUUID();
            this.cachedTarget = target;
        }
    }

    @Nullable
    public LivingEntity getTarget() {
        if (this.cachedTarget != null && !this.cachedTarget.isRemoved()) {
            return this.cachedTarget;
        } else if (this.targetUUID != null && this.level() instanceof ServerLevel) {
            this.cachedTarget = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.targetUUID);
            return this.cachedTarget;
        } else {
            return null;
        }
    }

    public Vec3 getStart() {
        return new Vec3(this.entityData.get(DATA_START));
    }

    public boolean isAttached() {
        return this.entityData.get(DATA_ATTACHED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_START, Vec3.ZERO.toVector3f());
        this.entityData.define(DATA_ATTACHED, false);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        Vector3f start = this.entityData.get(DATA_START);
        pCompound.putFloat("start_x", start.x());
        pCompound.putFloat("start_y", start.y());
        pCompound.putFloat("start_z", start.z());

        pCompound.putBoolean("attached", this.entityData.get(DATA_ATTACHED));

        if (this.targetUUID != null) {
            pCompound.putUUID("target", this.targetUUID);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_START, new Vector3f(pCompound.getFloat("start_x"), pCompound.getFloat("start_y"), pCompound.getFloat("start_z")));
        this.entityData.set(DATA_ATTACHED, pCompound.getBoolean("attached"));

        if (pCompound.hasUUID("target")) {
            this.targetUUID = pCompound.getUUID("target");
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (pResult.getEntity() instanceof LivingEntity entity && entity == this.getTarget()) {
            this.entityData.set(DATA_ATTACHED, true);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.getOwner() instanceof LivingEntity owner) {
                if (!JJKAbilities.hasToggled(owner, JJKAbilities.TIME_CELL_MOON_PALACE.get())) {
                    this.discard();
                }
            }

            LivingEntity target = this.getTarget();

            if (target != null && !target.isDeadOrDying() && !target.isRemoved()) {
                if (this.entityData.get(DATA_ATTACHED)) {
                    target.addEffect(new MobEffectInstance(JJKEffects.TWENTY_FOUR_FRAME_RULE.get(), 20, 0));
                }

                Vec3 src = this.position();
                Vec3 dst = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
                this.setDeltaMovement(dst.subtract(src).normalize().scale(SPEED));
            } else {
                this.discard();
            }
        }
    }
}
