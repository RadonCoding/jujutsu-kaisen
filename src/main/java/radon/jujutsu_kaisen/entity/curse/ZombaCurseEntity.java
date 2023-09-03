package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class ZombaCurseEntity extends CursedSpirit {
    private static final double ATTACK_RANGE = 32.0D;

    private static final RawAnimation SWING = RawAnimation.begin().thenLoop("attack.swing");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    public ZombaCurseEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
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
    protected boolean canPerformSorcery() {
        return false;
    }

    @Override
    protected boolean hasMeleeAttack() {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        LivingEntity target = this.getTarget();

        if (target != null) {
            if (this.distanceTo(target) <= ATTACK_RANGE) {
                AbilityHandler.trigger(this, JJKAbilities.SKY_STRIKE.get());
            } else {
                this.teleportTowards(target);
            }
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if (!this.level.isClientSide && pSource.getEntity() instanceof LivingEntity) {
            this.teleport();
        }
        return super.hurt(pSource, pAmount);
    }

    private void teleport() {
        if (!this.level.isClientSide && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double d1 = this.getY() + (double)(this.random.nextInt(64) - 32);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            this.teleport(d0, d1, d2);
        }
    }

    private void teleportTowards(Entity pTarget) {
        Vec3 pos = new Vec3(this.getX() - pTarget.getX(), this.getY(0.5D) - pTarget.getEyeY(), this.getZ() - pTarget.getZ()).normalize();
        double d0 = 16.0D;
        double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - pos.x * d0;
        double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - pos.y * d0;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - pos.z * d0;
        this.teleport(d1, d2, d3);
    }

    private void teleport(double pX, double pY, double pZ) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(pX, pY, pZ);

        while (pos.getY() > this.level.getMinBuildHeight() && !this.level.getBlockState(pos).getMaterial().blocksMotion()) {
            pos.move(Direction.DOWN);
        }
        BlockState blockstate = this.level.getBlockState(pos);
        boolean flag = blockstate.getMaterial().blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);

        if (flag && !flag1) {
            Vec3 current = this.position();
            boolean success = this.randomTeleport(pX, pY, pZ, true);

            if (success) {
                this.level.gameEvent(GameEvent.TELEPORT, current, GameEvent.Context.of(this));

                if (!this.isSilent()) {
                    this.level.playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_1;
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
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.SKY_STRIKE.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    private PlayState swingPredicate(AnimationState<ZombaCurseEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Idle", animationState -> animationState.setAndContinue(IDLE)));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
    }
}
