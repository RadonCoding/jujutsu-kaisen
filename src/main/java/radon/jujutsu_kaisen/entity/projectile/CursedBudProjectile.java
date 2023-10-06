package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.effect.DisasterPlantEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CursedBudProjectile extends JujutsuProjectile implements GeoEntity {
    public static final int DELAY = 20;
    private static final int DURATION = 3 * 20;
    private static final int EFFECT = 5 * 20;
    private static final double SPEED = 3.0D;
    private static final float DAMAGE = 5.0F;

    private boolean plant;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CursedBudProjectile(EntityType<? extends Projectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public CursedBudProjectile(LivingEntity pShooter) {
        super(JJKEntities.CURSED_BUD.get(), pShooter.level(), pShooter);

        Vec3 look = HelperMethods.getLookAngle(pShooter);
        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), pShooter.getYRot(), pShooter.getXRot());
    }

    public CursedBudProjectile(LivingEntity pShooter, DisasterPlantEntity plant) {
        super(JJKEntities.CURSED_BUD.get(), pShooter.level(), pShooter);

        Vec3 look = HelperMethods.getLookAngle(plant);
        Vec3 spawn = new Vec3(plant.getX(), plant.getEyeY() - (this.getBbHeight() / 2.0F), plant.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), plant.getYRot(), plant.getXRot());

        this.plant = true;
    }


    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (this.level().isClientSide) return;

        if (this.getOwner() instanceof LivingEntity owner) {
            if ((pResult.getEntity() instanceof LivingEntity living && owner.canAttack(living)) && living != owner) {
                ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                living.addEffect(new MobEffectInstance(JJKEffects.CURSED_BUD.get(), (int) (EFFECT * cap.getAbilityPower(owner)), 0, false, false, false));
                living.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.CURSED_BUD.get()), DAMAGE * cap.getAbilityPower(owner));
            }
        }
        this.discard();
    }

    @Override
    public void tick() {
        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.level().isClientSide) return;

            if (!this.plant && this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    if (this.getTime() % 5 == 0) {
                        owner.swing(InteractionHand.MAIN_HAND);
                    }
                    Vec3 look = HelperMethods.getLookAngle(owner);
                    Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
                    this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
                }
            } else if (this.getTime() >= DURATION) {
                this.discard();
            } else if (this.plant) {
                if (this.getTime() == 0) {
                    this.setDeltaMovement(HelperMethods.getLookAngle(this).scale(SPEED));
                } else if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    this.discard();
                }
            } else if (this.getTime() >= DELAY) {
                if (this.getTime() == DELAY) {
                    this.setDeltaMovement(HelperMethods.getLookAngle(owner).scale(SPEED));
                } else if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    this.discard();
                }
            }
        }
        super.tick();
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("plant", this.plant);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.plant = pCompound.getBoolean("plant");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
