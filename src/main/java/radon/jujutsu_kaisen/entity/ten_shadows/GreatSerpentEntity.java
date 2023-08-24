package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.TenShadowsSummon;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class GreatSerpentEntity extends TenShadowsSummon {
    private static final RawAnimation BITE = RawAnimation.begin().thenPlay("attack.bite");

    private static final int MAX_SEGMENTS = 24;

    private GreatSerpentSegmentEntity[] segments;

    public GreatSerpentEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GreatSerpentEntity(LivingEntity owner, boolean tame) {
        super(JJKEntities.GREAT_SERPENT.get(), owner.level);

        this.setTame(tame);
        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle().multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);

        this.createGoals();

        this.init();
    }

    private PlayState bitePredicate(AnimationState<GreatSerpentEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(BITE);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Bite", this::bitePredicate));
    }

    private void breakBlocks() {
        for (int i = 0; i < this.segments.length + 1; i++) {
            AABB bounds = i == 0 ? this.getBoundingBox() : this.segments[i - 1].getBoundingBox();

            BlockPos.betweenClosedStream(bounds).forEach(pos -> {
                BlockState state = this.level.getBlockState(pos);

                if (state.getFluidState().isEmpty() && state.canOcclude() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                    this.level.destroyBlock(pos, false);
                }
            });
        }
    }

    @Override
    public float getStepHeight() {
        return 2.0F;
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);

        JJKPartEntity.assignPartIDs(this);
    }

    private void init() {
        this.segments = new GreatSerpentSegmentEntity[MAX_SEGMENTS];

        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i] = new GreatSerpentSegmentEntity(this);
        }
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return this.segments;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);

        if (!this.level.isClientSide) {
            for (GreatSerpentSegmentEntity seg : this.segments) {
                seg.kill();
            }
        }
    }

    private void moveSegments() {
        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i].tick();

            Entity leader = i == 0 ? this : this.segments[i - 1];
            double followX = leader.getX();
            double followY = leader.getY();
            double followZ = leader.getZ();

            float angle = (((leader.getYRot() + 180.0F) * Mth.PI) / 180.0F);

            double force = 0.05D + (1.0D / (i + 1)) * 0.5D;

            double idealX = -Mth.sin(angle) * force;
            double idealZ = Mth.cos(angle) * force;

            double groundY = this.segments[i].isInWall() ? followY + 2.0F : followY;
            double idealY = (groundY - followY) * force;

            Vec3 diff = new Vec3(this.segments[i].getX() - followX, this.segments[i].getY() - followY, this.segments[i].getZ() - followZ);
            diff = diff.normalize();

            diff = diff.add(idealX, idealY, idealZ).normalize();

            double f = i == 0 ? 1.271D : 0.9D;

            double destX = followX + f * diff.x();
            double destY = followY + f * diff.y();
            double destZ = followZ + f * diff.z();

            this.segments[i].setPos(destX, destY, destZ);

            double distance = Mth.sqrt((float) (diff.x() * diff.x() + diff.z() * diff.z()));
            this.segments[i].setRot((float) (Math.atan2(diff.z(), diff.x()) * 180.0D / Math.PI) + 90.0F, -(float) (Math.atan2(diff.y(), distance) * 180.0D / Math.PI));
         }
    }

    @Override
    public void tick() {
        if (this.segments == null) {
            this.init();
        } else {
            super.tick();

            this.yHeadRot = this.getYRot();

            if (!this.level.isClientSide) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    this.breakBlocks();
                }
            }
            this.moveSegments();
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return this.segments != null;
    }

    private void createGoals() {
        int target = 1;
        int goal = 1;

        this.goalSelector.addGoal(goal++, new FloatGoal(this));
        this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.8D, true));
        this.goalSelector.addGoal(goal++, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(goal++, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, true));

        this.targetSelector.addGoal(target++, new HurtByTargetGoal(this));

        if (this.isTame()) {
            this.goalSelector.addGoal(goal++, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));

            this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
        } else {
            this.targetSelector.addGoal(target, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false,
                    entity -> this.participants.contains(entity.getUUID())));
        }
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.MAX_HEALTH, 3 * 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3 * 2.0D);
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.GREAT_SERPENT.get();
    }
}
