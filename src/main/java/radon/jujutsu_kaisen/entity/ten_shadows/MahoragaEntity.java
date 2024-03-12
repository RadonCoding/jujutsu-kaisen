package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.List;

public class MahoragaEntity extends TenShadowsSummon {
    public static EntityDataAccessor<Integer> DATA_SLASH = SynchedEntityData.defineId(MahoragaEntity.class, EntityDataSerializers.INT);
    public static EntityDataAccessor<Boolean> DATA_BATTLE = SynchedEntityData.defineId(MahoragaEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation IDLE_BATTLE = RawAnimation.begin().thenLoop("misc.idle_battle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation SLASH = RawAnimation.begin().thenPlay("attack.slash");

    private static final double SLASH_LAUNCH = 5.0D;

    private static final int SLASH_DURATION = 20;
    private static final int RITUAL_DURATION = 3 * 20;

    private boolean healing;

    public MahoragaEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public MahoragaEntity(LivingEntity owner, boolean tame) {
        this(JJKEntities.MAHORAGA.get(), owner.level());

        this.setTame(tame);
        this.setOwner(owner);

        Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
        Vec3 pos = owner.position()
                .subtract(direction.multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if (!this.isTame() && this.getTime() <= RITUAL_DURATION) return false;
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean isNoAi() {
        return (!this.isTame() && this.getTime() <= RITUAL_DURATION) || super.isNoAi();
    }

    @Override
    protected float ridingOffset(@NotNull Entity pEntity) {
        return this.getBbHeight() - 0.35F;
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean canFly() {
        return false;
    }

    @Override
    public boolean hasMeleeAttack() {
        return true;
    }

    @Override
    public boolean hasArms() {
        return true;
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public boolean canChant() {
        return false;
    }

    @Override
    public float getStepHeight() {
        return 2.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 5 * 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 5 * 2.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_SLASH, 0);
        this.entityData.define(DATA_BATTLE, false);
    }

    private PlayState walkRunIdlePredicate(AnimationState<MahoragaEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else {
            return animationState.setAndContinue(this.entityData.get(DATA_BATTLE) ? IDLE_BATTLE : IDLE);
        }
    }

    private PlayState swingPredicate(AnimationState<MahoragaEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState slashPredicate(AnimationState<MahoragaEntity> animationState) {
        int slash = this.entityData.get(DATA_SLASH);

        if (slash > 0) {
            return animationState.setAndContinue(SLASH);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run/Idle", this::walkRunIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Slash", this::slashPredicate));
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        boolean result = super.doHurtTarget(pEntity);

        if (result) {
            if (!(pEntity instanceof LivingEntity living)) return true;

            IJujutsuCapability cap = pEntity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return true;

            ISorcererData data = cap.getSorcererData();

            if (data == null) return true;

            if (data.getType() == JujutsuType.CURSE) {
                pEntity.hurt(this.damageSources().mobAttack(this), living.getMaxHealth());
            }
        }
        return result;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        LivingEntity target = this.getTarget();

        this.entityData.set(DATA_BATTLE, target != null);

        int slash = this.entityData.get(DATA_SLASH);

        if (slash > 0) {
            this.entityData.set(DATA_SLASH, --slash);
        } else {
            if (target != null) {
                if (this.onGround() && this.distanceTo(target) < 3.0D) {

                    if (this.doHurtTarget(target)) {
                        this.entityData.set(DATA_SLASH, SLASH_DURATION);

                        target.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(this).scale(SLASH_LAUNCH));
                        target.hurtMarked = true;

                        Vec3 center = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
                        ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                        this.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);
                    }
                }
            }
        }
     }

     private void setRitualOffset(Mob entity, int index, double padding) {
         entity.setNoAi(true);

         double x = entity.getX();
         double y = entity.getY();
         double z = entity.getZ();

         double distance = entity.getBbWidth() * 2;
         Vec3 look = RotationUtil.calculateViewVector(0.0F, entity.getYRot());
         Vec3 up = new Vec3(0.0D, 1.0D, 0.0D);
         Vec3 side = look.cross(up);
         Vec3 offset = side.scale(distance * (index < 3 ? 1 : -1))
                 .add(look.scale(padding + (index % 3) * 3.0D));
         entity.setPos(x + offset.x, y, z + offset.z);

         float yRot = entity.getYRot();

         if (index < 3) {
             yRot -= 90.0F;
         } else {
             yRot += 90.0F;
         }
         entity.setYRot(yRot);
         entity.yHeadRot = entity.getYRot();
         entity.yHeadRotO = entity.yHeadRot;
     }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.isTame()) {
            LivingEntity owner = this.getOwner();

            if (owner == null) return;

            IJujutsuCapability srcCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (srcCap == null) return;

            ITenShadowsData srcData = srcCap.getTenShadowsData();

            IJujutsuCapability dstCap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (dstCap == null) return;

            ITenShadowsData dstData = dstCap.getTenShadowsData();

            dstData.addAdapted(srcData.getAdapted());
            dstData.addAdapting(srcData.getAdapting());
        } else {
            this.playSound(JJKSounds.WOLF_HOWLING.get(), 3.0F, 1.0F);

            for (int i = 0; i < 6; i++) {
                DivineDogBlackEntity dog = new DivineDogBlackEntity(this, true);
                dog.setRitual(RITUAL_DURATION);
                this.setRitualOffset(dog, i, 1.5D);
                this.level().addFreshEntity(dog);
            }
            for (int i = 0; i < 6; i++) {
                ToadEntity toad = new ToadEntity(JJKEntities.TOAD.get(), this, false, true);
                toad.setRitual(RITUAL_DURATION);
                this.setRitualOffset(toad, i, 0.0D);
                this.level().addFreshEntity(toad);
            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (!this.isTame()) return;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        IJujutsuCapability srcCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (srcCap == null) return;

        ITenShadowsData srcData = srcCap.getTenShadowsData();

        IJujutsuCapability dstCap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (dstCap == null) return;

        ITenShadowsData dstData = dstCap.getTenShadowsData();

        dstData.addAdapted(srcData.getAdapted());
        dstData.addAdapting(srcData.getAdapting());
    }

    public void onAdaptation() {
        this.healing = this.getHealth() < this.getMaxHealth();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.healing) {
            this.heal(4.0F / 20);
            this.healing = this.getHealth() < this.getMaxHealth();
        }
    }

    @Override
    public float getExperience() {
        return this.isTame() ? super.getExperience() : SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
    }

    @Override
    public float getMaxEnergy() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.WHEEL.get());
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.MAHORAGA.get();
    }
}
