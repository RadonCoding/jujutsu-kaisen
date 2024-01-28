package radon.jujutsu_kaisen.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;

import java.util.List;

public class CursedEnergyFleshItem extends Item {
    public CursedEnergyFleshItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(String.format("item.%s.grade", JujutsuKaisen.MOD_ID),
                getGrade(pStack).getName().copy().withStyle(ChatFormatting.DARK_RED)));
    }

    public static SorcererGrade getGrade(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return SorcererGrade.values()[nbt.getInt("grade")];
    }

    public static void setGrade(ItemStack stack, SorcererGrade grade) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt("grade", grade.ordinal());
    }
}
