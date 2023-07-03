package radon.jujutsu_kaisen.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.ai.goal.TojiAttackGoal;
import radon.jujutsu_kaisen.entity.base.CurseEntity;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.concurrent.atomic.AtomicReference;

public class TojiFushiguroEntity extends SorcererEntity {
    protected TojiFushiguroEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public CursedTechnique getTechnique() {
        return CursedTechnique.NONE;
    }

    @Override
    public Trait getTrait() {
        return Trait.HEAVENLY_RESTRICTION;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(JJKItems.INVENTORY_CURSE.get()));

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get()));
        this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(1, new TojiAttackGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));

        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, CurseEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, SorcererEntity.class, true));
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
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }
}
