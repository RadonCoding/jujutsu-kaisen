package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.List;
import java.util.Set;

public class YutaOkkotsuEntity extends SorcererEntity {
    public YutaOkkotsuEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
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
    public @Nullable ICursedTechnique getTechnique() {
        return JJKCursedTechniques.MIMICRY.get();
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.OUTPUT_RCT.get());
    }

    @Override
    public Set<Ability> getUnlocked() {
        return Set.of(JJKAbilities.AUTHENTIC_MUTUAL_LOVE.get(), JJKAbilities.SIMPLE_DOMAIN.get(), JJKAbilities.RCT1.get(), JJKAbilities.RCT2.get(),
                JJKAbilities.OUTPUT_RCT.get());
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }


    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IMimicryData data = cap.getMimicryData();

        data.copy(JJKCursedTechniques.CURSED_SPEECH.get());

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.GREEN_HANDLE_KATANA.get()));
    }
}
