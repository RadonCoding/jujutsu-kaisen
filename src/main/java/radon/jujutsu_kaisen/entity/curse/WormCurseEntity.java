package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class WormCurseEntity extends CursedSpirit {
    private static final RawAnimation BITE = RawAnimation.begin().thenPlay("attack.bite");

    private static final int MAX_SEGMENTS = 24;

    private WormCurseSegmentEntity[] segments;

    public WormCurseEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
        return false;
    }

    @Override
    protected boolean hasMeleeAttack() {
        return true;
    }

    private PlayState bitePredicate(AnimationState<WormCurseEntity> animationState) {
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
                BlockState state = this.level().getBlockState(pos);

                if (state.getFluidState().isEmpty() && !state.canOcclude()) {
                    this.level().destroyBlock(pos, false);
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
        this.segments = new WormCurseSegmentEntity[MAX_SEGMENTS];

        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i] = new WormCurseSegmentEntity(this);
            this.segments[i].moveTo(this.getX() + 0.1D * i, this.getY() + 0.5D, this.getZ() + 0.1D * i, this.random.nextFloat() * 360.0F, 0.0F);
        }
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return this.segments;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);

        if (!this.level().isClientSide) {
            for (WormCurseSegmentEntity seg : this.segments) {
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

            double f = i == 0 ? 0.988D : 0.934D;

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

            if (!this.level().isClientSide) {
                if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
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

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes().add(Attributes.MOVEMENT_SPEED, 3 * 0.32D);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_2;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }
}
