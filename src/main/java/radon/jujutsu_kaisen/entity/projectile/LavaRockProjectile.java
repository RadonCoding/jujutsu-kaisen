package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class LavaRockProjectile extends JujutsuProjectile {
    private static final float DAMAGE = 10.0F;
    private static final float SPEED = 1.0F;

    @Nullable
    private UUID targetUUID;
    @Nullable
    private LivingEntity cachedTarget;

    public LavaRockProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public LavaRockProjectile(LivingEntity owner, float power, LivingEntity target) {
        super(JJKEntities.LAVA_ROCK.get(), owner.level(), owner, power);

        this.setTarget(target);

        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(HelperMethods.getLookAngle(owner));
        this.moveTo(spawn.x, spawn.y, spawn.z, owner.getYRot(), owner.getXRot());
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
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (this.level().isClientSide) return;

        Entity entity = pResult.getEntity();

        if (this.getOwner() instanceof LivingEntity owner) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            DomainExpansionEntity domain = cap.getSummonByClass((ServerLevel) this.level(), DomainExpansionEntity.class);

            if (domain == null) return;

            entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, null), DAMAGE * this.getPower());
            this.discard();
        }
    }

    @Override
    public void tick() {
        this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ(), 0.0D, 0.0D, 0.0D);

        if (!this.level().isClientSide) {
            if (this.getOwner() instanceof LivingEntity owner) {
                ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                DomainExpansionEntity domain = cap.getSummonByClass((ServerLevel) this.level(), DomainExpansionEntity.class);

                if (domain == null || !domain.checkSureHitEffect() || !JJKAbilities.hasToggled(owner, JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get())) {
                    this.discard();
                }
            }

            LivingEntity target = this.getTarget();

            if (target != null && !target.isDeadOrDying() && !target.isRemoved()) {
                this.setDeltaMovement(target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D)
                        .subtract(this.position()).normalize().scale(SPEED));
            } else {
                this.discard();
            }
        }
        super.tick();
    }
}
