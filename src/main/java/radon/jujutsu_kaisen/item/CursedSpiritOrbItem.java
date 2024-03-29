package radon.jujutsu_kaisen.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;

import java.util.List;

public class CursedSpiritOrbItem extends Item {
    public CursedSpiritOrbItem(Properties pProperties) {
        super(pProperties);
    }

    public static AbsorbedCurse getAbsorbed(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return new AbsorbedCurse(nbt.getCompound("curse"));
    }

    public static void setAbsorbed(ItemStack stack, AbsorbedCurse curse) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.put("curse", curse.serializeNBT());
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        MutableComponent name = super.getName(pStack).copy();
        return name.withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        AbsorbedCurse curse = getAbsorbed(pStack);
        pTooltipComponents.add(Component.translatable(String.format("item.%s.curse", JujutsuKaisen.MOD_ID), curse.getName().copy().withStyle(ChatFormatting.DARK_RED)));
        pTooltipComponents.add(Component.translatable(String.format("item.%s.experience", JujutsuKaisen.MOD_ID),
                Component.literal(Float.toString(CurseManipulationUtil.getCurseExperience(curse))).withStyle(ChatFormatting.GREEN)));
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        IJujutsuCapability cap = pEntityLiving.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) {
            pEntityLiving.addEffect(new MobEffectInstance(MobEffects.POISON, 10 * 20, 1));
            return stack;
        }

        ISorcererData sorcererData = cap.getSorcererData();

        if (!sorcererData.hasActiveTechnique(JJKCursedTechniques.CURSE_MANIPULATION.get())) {
            pEntityLiving.addEffect(new MobEffectInstance(MobEffects.POISON, 10 * 20, 1));
            return stack;
        }

        ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();
        curseManipulationData.addCurse(getAbsorbed(pStack));

        return stack;
    }
}

