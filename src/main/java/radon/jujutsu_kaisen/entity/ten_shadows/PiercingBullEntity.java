package radon.jujutsu_kaisen.entity.ten_shadows;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.*;

public class PiercingBullEntity extends TenShadowsSummon {
    private static final float DAMAGE = 5.0F;
    private static final int INTERVAL = 20;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    public PiercingBullEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public PiercingBullEntity(LivingEntity owner, boolean tame) {
        this(JJKEntities.PIERCING_BULL.get(), owner.level());

        this.setTame(tame, false);
        this.setOwner(owner);

        Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
        Vec3 pos = owner.position()
                .subtract(direction.multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setPathfindingMalus(PathType.LEAVES, 0.0F);
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
    public boolean canChant() {
        return false;
    }

    @Override
    public boolean hasMeleeAttack() {
        return false;
    }

    @Override
    public boolean hasArms() {
        return false;
    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        LivingEntity target = this.getTarget();

        if (target == null) return;

        this.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());

        if (!this.onGround() && !this.isInFluidType() && !this.isSprinting() || this.getTime() % INTERVAL != 0) return;

        this.setSprinting(true);

        this.move(MoverType.SELF, target.position().subtract(this.position()).normalize().scale(this.distanceTo(target)));

        float distance = this.distanceTo(target);

        IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, this.level(), this, this.getBoundingBox().inflate(1.0D))) {
            if (!entity.hurt(this.damageSources().mobAttack(this), DAMAGE * distance * data.getAbilityOutput())) continue;

            entity.setDeltaMovement(this.position().subtract(entity.position()).normalize().reverse().scale(data.getAbilityOutput()));
            entity.hurtMarked = true;

            Vec3 center = new Vec3(entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0F), entity.getZ());

            ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
            this.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                    4.0F, (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F);

            if (entity == target) {
                this.setSprinting(false);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 3 * 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 3 * 2.0D)
                .add(Attributes.STEP_HEIGHT, 2.0F);
    }

    private PlayState walkRunIdlePredicate(AnimationState<PiercingBullEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else {
            return animationState.setAndContinue(IDLE);
        }
    }

    private PlayState swingPredicate(AnimationState<PiercingBullEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run/Idle", this::walkRunIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", 2, this::swingPredicate));
    }

    @Override
    public float getExperience() {
        return this.isTame() ? super.getExperience() : SorcererGrade.GRADE_1.getRequiredExperience();
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.PIERCING_BULL.get();
    }
}
