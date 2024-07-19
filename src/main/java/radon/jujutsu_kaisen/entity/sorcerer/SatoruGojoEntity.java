package radon.jujutsu_kaisen.entity.sorcerer;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.item.registry.JJKItems;

import java.util.List;
import java.util.Set;

public class SatoruGojoEntity extends SorcererEntity {
    private final ItemStack blindfold;

    public SatoruGojoEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
        super(pType, pLevel);

        this.blindfold = new ItemStack(JJKItems.BLINDFOLD.get());
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
    @Nullable
    public CursedTechnique getTechnique() {
        return JJKCursedTechniques.LIMITLESS.get();
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.SIX_EYES);
    }

    @Override
    public Set<Ability> getUnlocked() {
        return Set.of(JJKAbilities.UNLIMITED_VOID.get(), JJKAbilities.SIMPLE_DOMAIN.get(), JJKAbilities.DOMAIN_AMPLIFICATION.get(),
                JJKAbilities.ZERO_POINT_TWO_SECOND_DOMAIN_EXPANSION.get(), JJKAbilities.RCT1.get(), JJKAbilities.RCT2.get(), JJKAbilities.RCT3.get());
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        LivingEntity target = this.getTarget();

        boolean wear = false;

        if (target == null) {
            wear = true;
        } else {
            IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                ISorcererData data = cap.getSorcererData();
                wear = data.getExperience() >= SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
            }
        }
        this.setItemSlot(EquipmentSlot.HEAD, wear ? this.blindfold : ItemStack.EMPTY);
    }
}
