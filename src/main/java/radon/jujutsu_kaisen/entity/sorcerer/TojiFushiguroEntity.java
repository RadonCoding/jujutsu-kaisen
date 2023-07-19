package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.entity.player.Player;
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
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableCurseGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableSorcererGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.projectile.BulletProjectile;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TojiFushiguroEntity extends SorcererEntity implements RangedAttackMob {
    private static final int PLAYFUL_CLOUD = 0;
    private static final int INVERTED_SPEAR_OF_HEAVEN = 1;
    private static final int PISTOL = 2;

    private final MeleeAttackGoal melee = new MeleeAttackGoal(this, 1.0D, false);
    private final RangedAttackGoal ranged = new RangedAttackGoal(this, 1.0D, 10, 15.0F);

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

        ItemStack inventory = new ItemStack(JJKItems.INVENTORY_CURSE.get());
        InventoryCurseItem.addItem(inventory, PLAYFUL_CLOUD, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));
        InventoryCurseItem.addItem(inventory, INVERTED_SPEAR_OF_HEAVEN, new ItemStack(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get()));
        InventoryCurseItem.addItem(inventory, PISTOL, new ItemStack(JJKItems.PISTOL.get()));
        this.setItemSlot(EquipmentSlot.CHEST, inventory);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SorcererGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableCurseGoal(this, true));
        this.targetSelector.addGoal(5, new NearestAttackableSorcererGoal(this,true));
    }

    private void pickWeapon(LivingEntity target) {
        this.goalSelector.removeGoal(this.melee);
        this.goalSelector.removeGoal(this.ranged);

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
        ItemStack stack = InventoryCurseItem.getItem(inventory, result.get());

        if (!this.getMainHandItem().is(stack.getItem())) {
            this.setItemInHand(InteractionHand.MAIN_HAND, stack);
        }

        if (stack.is(JJKItems.PISTOL.get())) {
            this.goalSelector.addGoal(1, this.ranged);
        } else {
            this.goalSelector.addGoal(1, this.melee);
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        if (pTarget != null && this.getTarget() != pTarget) {
            this.pickWeapon(pTarget);
        }
        super.setTarget(pTarget);
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.getTarget();

        if (target != null && this.tickCount % 5 == 0) {
            this.pickWeapon(target);
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity pTarget, float pVelocity) {
        ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);

        BulletProjectile bullet = new BulletProjectile(this);
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.3333333333333333D) - bullet.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        bullet.shoot(d0, d1 + d3 * (double)0.2F, d2, BulletProjectile.SPEED, 0.0F);
        this.level.addFreshEntity(bullet);

        this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                JJKSounds.GUN.get(), SoundSource.MASTER, 2.0F, 1.0F / (HelperMethods.RANDOM.nextFloat() * 0.4F + 0.8F));
        stack.hurtAndBreak(1, this, entity -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
    }
}
