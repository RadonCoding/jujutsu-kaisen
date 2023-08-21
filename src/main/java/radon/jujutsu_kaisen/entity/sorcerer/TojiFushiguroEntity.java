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
import net.minecraft.world.entity.monster.RangedAttackMob;
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
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.ai.goal.LookAtTargetGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableCurseGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableSorcererGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.PistolItem;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TojiFushiguroEntity extends SorcererEntity implements RangedAttackMob {
    private static final int PLAYFUL_CLOUD = 0;
    private static final int INVERTED_SPEAR_OF_HEAVEN = 1;
    private static final int PISTOL = 2;

    private static final int SHOOT_INTERVAL = 20;

    private final MeleeAttackGoal melee = new MeleeAttackGoal(this, 1.0D, true);
    private final RangedAttackGoal ranged = new RangedAttackGoal(this, 1.0D, SHOOT_INTERVAL, 15.0F);

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
    public @NotNull List<Trait> getTraits() {
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

        ItemStack inventory = new ItemStack(JJKItems.INVENTORY_CURSE.get());
        InventoryCurseItem.addItem(inventory, PLAYFUL_CLOUD, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));
        InventoryCurseItem.addItem(inventory, INVERTED_SPEAR_OF_HEAVEN, new ItemStack(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get()));
        InventoryCurseItem.addItem(inventory, PISTOL, new ItemStack(JJKItems.PISTOL.get()));
        this.setItemSlot(EquipmentSlot.CHEST, inventory);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SorcererGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableCurseGoal(this, false));
        this.targetSelector.addGoal(4, new NearestAttackableSorcererGoal(this,false));
    }

    private void pickWeapon(LivingEntity target) {
        AtomicInteger result = new AtomicInteger(PLAYFUL_CLOUD);

        target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasToggled(JJKAbilities.INFINITY.get())) {
                result.set(INVERTED_SPEAR_OF_HEAVEN);
            }
        });

        if (this.distanceTo(target) >= 10.0D) {
            result.set(PISTOL);
        }

        ItemStack inventory = this.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack main = InventoryCurseItem.getItem(inventory, result.get());

        if (!this.getMainHandItem().is(main.getItem())) {
            this.setItemInHand(InteractionHand.MAIN_HAND, main);
        }

        if (main.is(JJKItems.PISTOL.get())) {
            this.goalSelector.removeGoal(this.melee);
            this.goalSelector.addGoal(1, this.ranged);
        } else {
            this.goalSelector.removeGoal(this.ranged);
            this.goalSelector.addGoal(1, this.melee);
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        LivingEntity target = this.getTarget();

        if (target != null) {
            this.pickWeapon(target);
        }
    }

    @Override
    public void tick() {
        super.tick();

        for (ClosedDomainExpansionEntity ignored : HelperMethods.getEntityCollisionsOfClass(ClosedDomainExpansionEntity.class, this.level, this.getBoundingBox())) {
            if (!this.getMainHandItem().is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                ItemStack inventory = this.getItemBySlot(EquipmentSlot.CHEST);
                ItemStack stack = InventoryCurseItem.getItem(inventory, INVERTED_SPEAR_OF_HEAVEN);
                this.setItemInHand(InteractionHand.MAIN_HAND, stack);
            }

            if (!this.isUsingItem()) {
                this.startUsingItem(InteractionHand.MAIN_HAND);
            }
            break;
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity pTarget, float pVelocity) {
        if (this.random.nextInt(10) != 0) return;

        ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);
        PistolItem.shoot(stack, this);
    }
}
