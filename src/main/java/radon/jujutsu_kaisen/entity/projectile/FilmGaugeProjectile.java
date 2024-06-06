package radon.jujutsu_kaisen.entity.projectile;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

import java.util.UUID;

public class FilmGaugeProjectile extends JujutsuProjectile {
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

    public FilmGaugeProjectile(LivingEntity owner, float power, LivingEntity target) {
        this(JJKEntities.FILM_GAUGE.get(), owner.level());

        this.setOwner(owner);
        this.setPower(power);

        this.setTarget(target);

        this.setPos(owner.getX(), owner.getY() + (owner.getBbHeight() / 2) - (this.getBbHeight() / 2), owner.getZ());
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

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.targetUUID != null) {
            pCompound.putUUID("target", this.targetUUID);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("target")) {
            this.targetUUID = pCompound.getUUID("target");
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        this.discard();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (this.level().isClientSide) return;

        if (!(pResult.getEntity() instanceof LivingEntity entity)) return;

        if (this.getOwner() instanceof LivingEntity owner) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            DomainExpansionEntity domain = data.getSummonByClass(DomainExpansionEntity.class);

            if (domain == null) return;

            if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, null), DAMAGE * this.getPower())) {
                entity.addEffect(new MobEffectInstance(JJKEffects.STUN, 20, 1, false, false, false));
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            if (this.getOwner() instanceof LivingEntity owner) {
                IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                ISorcererData data = cap.getSorcererData();

                DomainExpansionEntity domain = data.getSummonByClass(DomainExpansionEntity.class);

                if (domain == null || !domain.checkSureHitEffect()) {
                    this.discard();
                }
            }

            LivingEntity target = this.getTarget();

            if (target != null && !target.isDeadOrDying() && !target.isRemoved()) {
                Vec3 src = this.position();
                Vec3 dst = target.position().add(0.0D, target.getBbHeight() / 2, 0.0D);
                this.setDeltaMovement(dst.subtract(src).normalize().scale(SPEED));
            } else {
                this.discard();
            }
        }
        super.tick();
    }
}
