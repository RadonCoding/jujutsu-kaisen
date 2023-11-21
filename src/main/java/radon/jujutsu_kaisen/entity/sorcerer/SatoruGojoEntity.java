package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFloatGoal;
import radon.jujutsu_kaisen.entity.ai.goal.LookAtTargetGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableCurseGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.List;

public class SatoruGojoEntity extends SorcererEntity {
    public SatoruGojoEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean targetsCurses() {
        return true;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return CursedTechnique.LIMITLESS;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.SIX_EYES, Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public List<Ability> getUnlocked() {
        return List.of(JJKAbilities.SIMPLE_DOMAIN.get(), JJKAbilities.UNLIMITED_VOID.get(), JJKAbilities.DOMAIN_AMPLIFICATION.get(), JJKAbilities.ZERO_POINT_TWO_SECOND_DOMAIN_EXPANSION.get());
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.UNLIMITED_VOID.get();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(JJKItems.SATORU_CHESTPLATE.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(JJKItems.SATORU_LEGGINGS.get()));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(JJKItems.SATORU_BOOTS.get()));
    }

    @Override
    protected void customServerAiStep() {
        LivingEntity target = this.getTarget();

        if (target != null && target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (this.getGrade().ordinal() >= SorcererGrade.GRADE_1.ordinal()) {
                if (!this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                }
                return;
            }
        }

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(JJKItems.SATORU_BLINDFOLD.get()));
        }
    }
}
