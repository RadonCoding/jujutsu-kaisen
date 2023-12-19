package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.effect.DisasterPlantEntity;
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

    public CursedBudProjectile(LivingEntity owner, float power) {
        super(JJKEntities.CURSED_BUD.get(), owner.level(), owner, power);

        Vec3 look = owner.getLookAngle();
        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
        this.moveTo(spawn.x, spawn.y, spawn.z, owner.getYRot(), owner.getXRot());
    }

    public CursedBudProjectile(LivingEntity owner, float power, DisasterPlantEntity plant) {
        super(JJKEntities.CURSED_BUD.get(), owner.level(), owner, power);

        Vec3 look = plant.getLookAngle();
        Vec3 spawn = new Vec3(plant.getX(), plant.getEyeY() - (this.getBbHeight() / 2.0F), plant.getZ()).add(look);
        this.moveTo(spawn.x, spawn.y, spawn.z, plant.getYRot(), plant.getXRot());

        this.plant = true;
    }

    public void implant(LivingEntity victim) {
        if (this.getOwner() instanceof LivingEntity owner) {
            if (victim == owner) return;

            if (victim.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.CURSED_BUD.get()), DAMAGE * this.getPower())) {
                victim.addEffect(new MobEffectInstance(JJKEffects.CURSED_BUD.get(), (int) (EFFECT * this.getPower()), 0));
            }
        }
        this.discard();
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

        if (pResult.getEntity() instanceof LivingEntity living) {
            this.implant(living);
        }
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
                    Vec3 look = owner.getLookAngle();
                    Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
                    this.moveTo(spawn.x, spawn.y, spawn.z, owner.getYRot(), owner.getXRot());
                }
            } else if (this.getTime() >= DURATION) {
                this.discard();
            } else if (this.plant) {
                if (this.getTime() == 0) {
                    this.setDeltaMovement(this.getLookAngle().scale(SPEED));
                }
            } else if (this.getTime() == DELAY) {
                this.setDeltaMovement(owner.getLookAngle().scale(SPEED));
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
