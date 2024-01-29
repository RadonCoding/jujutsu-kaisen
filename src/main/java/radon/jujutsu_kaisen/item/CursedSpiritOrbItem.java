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
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

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
        AbsorbedCurse curse = getAbsorbed(pStack);
        pTooltipComponents.add(Component.translatable(String.format("item.%s.curse", JujutsuKaisen.MOD_ID), curse.getName().copy().withStyle(ChatFormatting.DARK_RED)));
        pTooltipComponents.add(Component.translatable(String.format("item.%s.experience", JujutsuKaisen.MOD_ID),
                Component.literal(Float.toString(JJKAbilities.getCurseExperience(curse))).withStyle(ChatFormatting.GREEN)));
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        if (pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!cap.hasTechnique(CursedTechnique.CURSE_MANIPULATION)) {
                pEntityLiving.addEffect(new MobEffectInstance(MobEffects.POISON, 10 * 20, 1));
                return stack;
            }
            cap.addCurse(getAbsorbed(pStack));
        }
        return stack;
    }
}

