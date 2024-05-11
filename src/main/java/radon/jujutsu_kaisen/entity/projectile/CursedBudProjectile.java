package radon.jujutsu_kaisen.entity.projectile;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

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
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.effect.DisasterPlantEntity;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CursedBudProjectile extends JujutsuProjectile implements GeoEntity {
    public static final int DELAY = 20;
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
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));
    }

    public CursedBudProjectile(LivingEntity owner, float power, DisasterPlantEntity plant) {
        super(JJKEntities.CURSED_BUD.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(plant);
        EntityUtil.offset(this, look, new Vec3(plant.getX(), plant.getEyeY() - (this.getBbHeight() / 2.0F), plant.getZ()).add(look));

        this.setDeltaMovement(look.scale(SPEED));

        this.plant = true;
    }

    public void implant(LivingEntity victim) {
        if (this.getOwner() instanceof LivingEntity owner) {
            if (victim == owner) return;

            if (victim.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.CURSED_BUD.get()), DAMAGE * this.getPower())) {
                IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                IAbilityData data = cap.getAbilityData();

                if (data.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get()) || data.hasToggled(JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) {
                    victim.addEffect(new MobEffectInstance(JJKEffects.CURSED_BUD, (int) (EFFECT * this.getPower()), 0));
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

        if (!(pResult.getEntity() instanceof LivingEntity living)) return;

        this.implant(living);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.level().isClientSide) return;

            if (!this.plant) {
                if (this.getTime() < DELAY) {
                    if (!owner.isAlive()) {
                        this.discard();
                    } else {
                        if (this.getTime() % 5 == 0) {
                            owner.swing(InteractionHand.MAIN_HAND);
                        }
                        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));
                    }
                } else if (this.getTime() == DELAY) {
                    this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
                }
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
