package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ai.goal.*;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.List;

public class SukunaRyomenEntity extends SorcererEntity {
    public SukunaRyomenEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public List<Ability> getUnlocked() {
        return List.of(JJKAbilities.SIMPLE_DOMAIN.get(), JJKAbilities.MALEVOLENT_SHRINE.get(), JJKAbilities.DOMAIN_AMPLIFICATION.get());
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
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 3.0F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BetterFloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(4, new SorcererGoal(this));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableSorcererGoal(this,false));
        this.targetSelector.addGoal(4, new NearestAttackableCurseGoal(this, false));
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(JJKItems.YUJI_CHESTPLATE.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(JJKItems.YUJI_LEGGINGS.get()));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(JJKItems.YUJI_BOOTS.get()));
    }

    public void convertTo() {
        HeianSukunaEntity entity = JJKEntities.HEIAN_SUKUNA.get().create(this.level());

        if (entity != null) {
            entity.copyPosition(this);
            entity.setBaby(this.isBaby());
            entity.setNoAi(this.isNoAi());
            if (this.hasCustomName()) {
                entity.setCustomName(this.getCustomName());
                entity.setCustomNameVisible(this.isCustomNameVisible());
            }

            if (this.isPersistenceRequired()) {
                entity.setPersistenceRequired();
            }

            entity.setInvulnerable(this.isInvulnerable());

            this.level().addFreshEntity(entity);

            Entity vehicle = this.getVehicle();

            if (vehicle != null) {
                this.stopRiding();
                entity.startRiding(vehicle, true);
            }
            this.discard();
        }
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);

        if (pReason == RemovalReason.KILLED) {
            this.convertTo();
        }
    }
}
