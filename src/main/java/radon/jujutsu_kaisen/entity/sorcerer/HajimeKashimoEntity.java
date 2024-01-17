package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

public class HajimeKashimoEntity extends SorcererEntity {
    public HajimeKashimoEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 2.5F;
    }

    @Override
    public int getCursedEnergyColor() {
        return 13893887;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @Nullable CursedEnergyNature getNature() {
        return CursedEnergyNature.LIGHTNING;
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.NYOI_STAFF.get()));
    }
}
