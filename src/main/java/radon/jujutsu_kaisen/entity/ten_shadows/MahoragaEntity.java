package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;
import radon.jujutsu_kaisen.sound.JJKSounds;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class MahoragaEntity extends TenShadowsSummon {
    public static EntityDataAccessor<Integer> DATA_SLASH = SynchedEntityData.defineId(MahoragaEntity.class, EntityDataSerializers.INT);
    public static EntityDataAccessor<Boolean> DATA_BATTLE = SynchedEntityData.defineId(MahoragaEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation IDLE_BATTLE = RawAnimation.begin().thenLoop("misc.idle.battle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation SLASH = RawAnimation.begin().thenPlay("attack.slash");

    private static final double SWING_LAUNCH = 5.0D;
    private static final float SWING_EXPLOSION = 2.5F;

    private static final int SLASH_DURATION = 20;
    private static final int RITUAL_DURATION = 3 * 20;

    public MahoragaEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean isInvulnerable() {
        return (!this.isTame() && this.getTime() <= RITUAL_DURATION) || super.isInvulnerable();
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() - 0.35D;
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
    public boolean canPerformSorcery() {
        return true;
    }

    @Override
    protected boolean hasMeleeAttack() {
        return true;
    }

    public MahoragaEntity(LivingEntity owner, boolean tame) {
        this(JJKEntities.MAHORAGA.get(), owner.level());

        this.setTame(tame);
        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle()
                        .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
    }

    @Override
    public float getStepHeight() {
        return 2.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 5 * 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
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

            if (!pEntity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return true;
            ISorcererData cap = pEntity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getType() == JujutsuType.CURSE) {
                pEntity.hurt(this.damageSources().mobAttack(this), living.getMaxHealth());
            }
        }
        return result;
    }

    @Override
    protected void customServerAiStep() {
        this.setSprinting(this.getDeltaMovement().lengthSqr() > 0.01D && this.moveControl.getSpeedModifier() > 1.0D);

        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(4.0F / 20);
        }

        LivingEntity target = this.getTarget();

        this.entityData.set(DATA_BATTLE, target != null && target.getMaxHealth() >= Player.MAX_HEALTH * 3);

        int slash = this.entityData.get(DATA_SLASH);

        if (slash > 0) {
            this.entityData.set(DATA_SLASH, --slash);
        } else {
            if (target != null) {
                if (this.onGround() && this.distanceTo(target) < 3.0D) {
                    this.entityData.set(DATA_SLASH, SLASH_DURATION);

                    target.setDeltaMovement(this.getLookAngle().scale(SWING_LAUNCH));
                    target.hurtMarked = true;

                    Vec3 explosionPos = new Vec3(this.getX(), this.getEyeY() - 0.2D, this.getZ()).add(this.getLookAngle());
                    this.level().explode(this, explosionPos.x(), explosionPos.y(), explosionPos.z(), SWING_EXPLOSION, false, Level.ExplosionInteraction.NONE);
                }
            }
        }

        ISorcererData cap = this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (DomainExpansionEntity domain : cap.getDomains((ServerLevel) this.level())) {
            if (!(domain instanceof ClosedDomainExpansionEntity closed) || closed.getTime() < closed.getRadius() * 2)
                continue;
            if (cap.isAdaptedTo(domain.getAbility())) domain.discard();
        }
     }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.isTame()) {
            LivingEntity owner = this.getOwner();

            if (owner != null) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(srcCap -> {
                    this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(dstCap -> {
                        dstCap.addAdapted(srcCap.getAdapted());
                        dstCap.addAdapting(srcCap.getAdapting());
                    });
                });
            }
        } else {
            this.playSound(JJKSounds.WOLF_HOWLING.get(), 3.0F, 1.0F);

            for (int i = 0; i < 6; i++) {
                DivineDogBlackEntity dog = new DivineDogBlackEntity(this, true);
                dog.setRitual(i, RITUAL_DURATION);
                this.level().addFreshEntity(dog);
            }
            for (int i = 0; i < 6; i++) {
                ToadEntity dog = new ToadEntity(JJKEntities.TOAD.get(), this, false, true);
                dog.setRitual(i, RITUAL_DURATION);
                this.level().addFreshEntity(dog);
            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (this.isTame()) {
            LivingEntity owner = this.getOwner();

            if (owner != null) {
                this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(srcCap -> {
                    owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(dstCap -> {
                        dstCap.addAdapted(srcCap.getAdapted());
                        dstCap.addAdapting(srcCap.getAdapting());
                    });
                });
            }
        }
    }

    private void breakBlocks() {
        AABB bounds = this.getBoundingBox();

        BlockPos.betweenClosedStream(bounds).forEach(pos -> {
            BlockState state = this.level().getBlockState(pos);

            if (state.getFluidState().isEmpty() && state.canOcclude() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                this.level().destroyBlock(pos, false);
            }
        });
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isTame()) {
            this.setNoAi(this.getTime() <= RITUAL_DURATION);
        }

        if (!this.level().isClientSide) {
            if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                this.breakBlocks();
            }
        }
    }

    @Override
    public float getExperience() {
        return Math.max(SorcererGrade.SPECIAL_GRADE.getRequiredExperience(), super.getExperience());
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
