package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.ai.goal.*;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.KamutokeDaggerItem;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class HeianSukunaEntity extends SorcererEntity {
    private static final EntityDataAccessor<Boolean> DATA_IDLE = SynchedEntityData.defineId(HeianSukunaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_BARRAGE = SynchedEntityData.defineId(HeianSukunaEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation BARRAGE = RawAnimation.begin().thenLoop("attack.barrage");
    private static final RawAnimation SIT = RawAnimation.begin().thenPlayAndHold("misc.sit");

    public HeianSukunaEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void setBarrage(int barrage) {
        this.entityData.set(DATA_BARRAGE, barrage);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_IDLE, false);
        this.entityData.define(DATA_BARRAGE, 0);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.TRISHULA_STAFF.get()));
        this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(JJKItems.KAMUTOKE_DAGGER.get()));
    }

    @Override
    public @NotNull SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return CursedTechnique.DISMANTLE_AND_CLEAVE;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE, Trait.DOMAIN_EXPANSION, Trait.SIMPLE_DOMAIN, Trait.STRONGEST);
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.MALEVOLENT_SHRINE.get();
    }

    @Override
    protected void customServerAiStep() {
        this.entityData.set(DATA_IDLE, this.getTarget() == null);

        int barrage = this.entityData.get(DATA_BARRAGE);

        if (barrage > 0) {
            this.entityData.set(DATA_BARRAGE, --barrage);
        }

        if (this.random.nextInt(5 * 20) == 0) {
            LivingEntity target = this.getTarget();

            if (target != null) {
                this.startUsingItem(InteractionHand.OFF_HAND);

                if (HelperMethods.getLookAtHit(this, KamutokeDaggerItem.RANGE) instanceof EntityHitResult hit && hit.getEntity() == target) {
                    this.stopUsingItem();
                }
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BetterFloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new SorcererGoal(this));
        this.goalSelector.addGoal(4, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableSorcererGoal(this, false));
        this.targetSelector.addGoal(4, new NearestAttackableCurseGoal(this, false));
    }

    private PlayState walkSitPredicate(AnimationState<HeianSukunaEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(WALK);
        } else if (this.entityData.get(DATA_IDLE)) {
            return animationState.setAndContinue(SIT);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<HeianSukunaEntity> animationState) {
        if (this.entityData.get(DATA_BARRAGE) > 0) {
            return animationState.setAndContinue(BARRAGE);
        } else if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Sit", this::walkSitPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
    }
}
