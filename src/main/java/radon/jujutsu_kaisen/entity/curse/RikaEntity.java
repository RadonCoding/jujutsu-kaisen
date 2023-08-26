package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ai.goal.LookAtTargetGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.ICommandable;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class RikaEntity extends SummonEntity implements ICommandable, ISorcerer {
    private static final int DURATION = 10 * 20;

    public static EntityDataAccessor<Boolean> DATA_OPEN = SynchedEntityData.defineId(RikaEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("misc.open");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    private boolean tame;

    public RikaEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public RikaEntity(LivingEntity owner, boolean tame) {
        this(JJKEntities.RIKA.get(), owner.level);

        this.setTame(true);
        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle()
                        .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z());

        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);

        this.moveControl = new FlyingMoveControl(this, 20, true);

        this.tame = tame;
    }

    @Override
    protected float getFlyingSpeed() {
        float speed = super.getFlyingSpeed();

        if (this.getTarget() != null) {
            speed *= 10.0F;
        } else {
            speed *= 5.0F;
        }
        return speed;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SorcererGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, true));
        this.goalSelector.addGoal(6, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_OPEN, false);
    }

    public void setOpen(boolean open) {
        this.entityData.set(DATA_OPEN, open);
    }

    public boolean isOpen() {
        return this.entityData.get(DATA_OPEN);
    }

    private PlayState openPredicate(AnimationState<RikaEntity> animationState) {
        if (this.isOpen()) {
            return animationState.setAndContinue(OPEN);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<RikaEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Idle", state -> state.setAndContinue(IDLE)));
        controllerRegistrar.add(new AnimationController<>(this, "Open", this::openPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("tame", this.tame);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.tame = pCompound.getBoolean("tame");
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.RIKA.get();
    }

    private void breakBlocks() {
        AABB bounds = this.getBoundingBox();

        BlockPos.betweenClosedStream(bounds).forEach(pos -> {
            BlockState state = this.level.getBlockState(pos);

            if (state.getFluidState().isEmpty() && state.canOcclude() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                this.level.destroyBlock(pos, false);
            }
        });
    }

    @Override
    public void tick() {
        LivingEntity owner = this.getOwner();

        if (!this.level.isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive() ||
                (!this.isDeadOrDying() && !JJKAbilities.hasToggled(owner, JJKAbilities.RIKA.get())))) {
            this.discard();
        } else {
            super.tick();

            if (!this.level.isClientSide) {
                if (!this.tame && this.getTime() >= DURATION) {
                    this.discard();
                }

                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    this.breakBlocks();
                }
                LivingEntity target = this.getTarget();
                this.setOpen(target != null && target.getMaxHealth() > Player.MAX_HEALTH);
            }
        }
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.PURE_LOVE.get());
    }

    @Override
    public @NotNull SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of();
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    @Override
    public boolean canChangeTarget() {
        return this.tame;
    }

    @Override
    public void changeTarget(LivingEntity target) {
        this.setTarget(target);
    }
}
