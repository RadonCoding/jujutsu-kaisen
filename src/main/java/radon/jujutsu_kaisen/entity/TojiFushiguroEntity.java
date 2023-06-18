package radon.jujutsu_kaisen.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.SorcererGrade;
import radon.jujutsu_kaisen.capability.SpecialTrait;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.concurrent.atomic.AtomicReference;

public class TojiFushiguroEntity extends SorcererEntity {
    protected TojiFushiguroEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(JJKItems.INVENTORY_CURSE.get()));

        this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            cap.setTrait(SpecialTrait.HEAVENLY_RESTRICTION);
            cap.setGrade(SorcererGrade.SPECIAL_GRADE);
        });

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get()));
        this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 50.0D)
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }

    private void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    private void pickWeapon(LivingEntity target) {
        AtomicReference<Item> result = new AtomicReference<>(JJKItems.PLAYFUL_CLOUD.get());

        target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasToggledAbility(JJKAbilities.INFINITY.get())) {
                result.set(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get());
            }
        });

        if (!this.getMainHandItem().is(result.get()) && this.getOffhandItem().is(result.get())) {
            ItemStack itemstack = this.getItemInHand(InteractionHand.OFF_HAND);
            this.setItemInHand(InteractionHand.OFF_HAND, this.getItemInHand(InteractionHand.MAIN_HAND));
            this.setItemInHand(InteractionHand.MAIN_HAND, itemstack);
            this.stopUsingItem();
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        if (pTarget != null) {
            this.pickWeapon(pTarget);
        }
        super.setTarget(pTarget);
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.getTarget();

        if (target != null) {
            if (this.distanceTo(target) >= 5.0D) {
                Vec3 direction = target.position().subtract(this.position()).normalize();

                Vec3 forward = this.getLookAngle();
                double dotProduct = forward.dot(direction);
                double angle = Math.acos(dotProduct);

                double threshold = Math.toRadians(45);

                if (angle <= threshold) {
                    this.setDeltaMovement(target.position().subtract(this.position()).normalize().scale(2.5D));
                }
            }
        }
    }
}
