package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
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
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.effect.DisasterPlantEntity;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
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

    public CursedBudProjectile(EntityType<? extends Projectile> pType, Level level) {
        super(pType, level);
    }

    public CursedBudProjectile(LivingEntity owner, float power) {
        super(JJKEntities.CURSED_BUD.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
        this.setPos(spawn.x, spawn.y, spawn.z);

        EntityUtil.applyOffset(this, look);
    }

    public CursedBudProjectile(LivingEntity owner, float power, DisasterPlantEntity plant) {
        super(JJKEntities.CURSED_BUD.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(plant);
        Vec3 spawn = new Vec3(plant.getX(), plant.getEyeY() - (this.getBbHeight() / 2.0F), plant.getZ()).add(look);
        this.setPos(spawn.x, spawn.y, spawn.z);

        EntityUtil.applyOffset(this, look);

        this.plant = true;
    }

    public void implant(LivingEntity victim) {
        if (this.getOwner() instanceof LivingEntity owner) {
            if (victim == owner) return;

            if (victim.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.CURSED_BUD.get()), DAMAGE * this.getPower())) {
                if (JJKAbilities.hasToggled(victim, JJKAbilities.CURSED_ENERGY_FLOW.get()) || JJKAbilities.hasToggled(victim, JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) {
                    victim.addEffect(new MobEffectInstance(JJKEffects.CURSED_BUD.get(), (int) (EFFECT * this.getPower()), 0));
                }
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
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.level().isClientSide) return;

            if (!this.plant && this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    if (this.getTime() % 5 == 0) {
                        owner.swing(InteractionHand.MAIN_HAND);
                    }
                    Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                    Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
                    this.setPos(spawn.x, spawn.y, spawn.z);

                    double d0 = look.horizontalDistance();
                    this.setYRot((float) (Mth.atan2(look.x, look.z) * (double) (180.0F / (float) Math.PI)));
                    this.setXRot((float) (Mth.atan2(look.y, d0) * (double) (180.0F / (float) Math.PI)));
                    this.yRotO = this.getYRot();
                    this.xRotO = this.getXRot();
                }
            } else if (this.getTime() >= DURATION) {
                this.discard();
            } else if (this.plant) {
                if (this.getTime() - 1 == 0) {
                    this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
                }
            } else if (this.getTime() == DELAY) {
                this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
            }
        }
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
