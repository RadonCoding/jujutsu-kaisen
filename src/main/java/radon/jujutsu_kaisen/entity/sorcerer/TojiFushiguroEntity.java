package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.ai.goal.AvoidDomainsGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableCurseGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableSorcererGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TojiFushiguroEntity extends SorcererEntity {
    public TojiFushiguroEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
    public @Nullable List<Trait> getTraits() {
        return List.of(Trait.HEAVENLY_RESTRICTION);
    }

    @Override
    public boolean isCurse() {
        return false;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(JJKItems.INVENTORY_CURSE.get()));

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get()));
        this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidDomainsGoal(this, 1.6D, 1.4D));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new SorcererGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableCurseGoal(this, true));
        this.targetSelector.addGoal(5, new NearestAttackableSorcererGoal(this,true));
    }

    private void pickWeapon(LivingEntity target) {
        AtomicReference<Item> result = new AtomicReference<>(JJKItems.PLAYFUL_CLOUD.get());

        target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasToggled(JJKAbilities.INFINITY.get())) {
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
}
