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
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.UUID;

public class FilmGaugeProjectile extends JujutsuProjectile {
    private static final EntityDataAccessor<Vector3f> DATA_START = SynchedEntityData.defineId(FilmGaugeProjectile.class, EntityDataSerializers.VECTOR3);

    private static final float SPEED = 3.0F;
    private static final float DAMAGE = 10.0F;

    @Nullable
    private UUID targetUUID;
    @Nullable
    private LivingEntity cachedTarget;

    public FilmGaugeProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public FilmGaugeProjectile(LivingEntity owner, float power, LivingEntity target, DomainExpansionCenterEntity center) {
        this(JJKEntities.FILM_GAUGE.get(), owner.level());

        this.setOwner(owner);
        this.setPower(power);

        this.setTarget(target);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(center);
        EntityUtil.offset(this, look, new Vec3(center.getX(), center.getY() + (center.getBbHeight() / 2.0F) - (this.getBbHeight() / 2.0F), center.getZ()).add(look));

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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_START, Vec3.ZERO.toVector3f());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        Vector3f start = this.entityData.get(DATA_START);
        pCompound.putFloat("start_x", start.x);
        pCompound.putFloat("start_y", start.y);
        pCompound.putFloat("start_z", start.z);

        if (this.targetUUID != null) {
            pCompound.putUUID("target", this.targetUUID);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_START, new Vector3f(pCompound.getFloat("start_x"), pCompound.getFloat("start_y"), pCompound.getFloat("start_z")));

        if (pCompound.hasUUID("target")) {
            this.targetUUID = pCompound.getUUID("target");
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (this.level().isClientSide) return;

        if (!(pResult.getEntity() instanceof LivingEntity entity)) return;

        if (this.getOwner() instanceof LivingEntity owner) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            DomainExpansionEntity domain = cap.getSummonByClass(DomainExpansionEntity.class);

            if (domain == null) return;

            if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, null), DAMAGE * this.getPower())) {
                entity.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 20, 1, false, false, false));
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            if (this.getOwner() instanceof LivingEntity owner) {
                ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                DomainExpansionEntity domain = cap.getSummonByClass(DomainExpansionEntity.class);

                if (domain == null || !domain.checkSureHitEffect() || !JJKAbilities.hasToggled(owner, JJKAbilities.TIME_CELL_MOON_PALACE.get())) {
                    this.discard();
                }
            }

            LivingEntity target = this.getTarget();

            if (target != null && !target.isDeadOrDying() && !target.isRemoved()) {
                Vec3 src = this.position();
                Vec3 dst = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
                this.setDeltaMovement(dst.subtract(src).normalize().scale(SPEED));
            } else {
                this.discard();
            }
        }
        super.tick();
    }
}
