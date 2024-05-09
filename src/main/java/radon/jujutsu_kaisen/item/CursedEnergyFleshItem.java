package radon.jujutsu_kaisen.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;

import java.util.List;

public class CursedEnergyFleshItem extends Item {
    public CursedEnergyFleshItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        SorcererGrade grade = pStack.get(JJKDataComponentTypes.SORCERER_GRADE);

        if (grade == null) return;

        pTooltipComponents.add(Component.translatable(String.format("item.%s.grade", JujutsuKaisen.MOD_ID),
                grade.getName().copy().withStyle(ChatFormatting.DARK_RED)));
    }
}
