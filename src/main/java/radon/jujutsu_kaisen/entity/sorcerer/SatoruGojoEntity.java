package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.List;
import java.util.Set;

public class SatoruGojoEntity extends SorcererEntity {
    public SatoruGojoEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(JJKItems.BLINDFOLD.get()));
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 3.0F;
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return JJKCursedTechniques.LIMITLESS.get();
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.SIX_EYES);
    }

    @Override
    public Set<Ability> getUnlocked() {
        return Set.of(JJKAbilities.UNLIMITED_VOID.get(), JJKAbilities.SIMPLE_DOMAIN.get(), JJKAbilities.DOMAIN_AMPLIFICATION.get(),
                JJKAbilities.ZERO_POINT_TWO_SECOND_DOMAIN_EXPANSION.get(), JJKAbilities.RCT1.get(),  JJKAbilities.RCT2.get(), JJKAbilities.RCT3.get());
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }
}
