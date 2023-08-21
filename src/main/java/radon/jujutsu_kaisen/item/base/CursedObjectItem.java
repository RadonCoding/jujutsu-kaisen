package radon.jujutsu_kaisen.item.base;

import net.minecraft.ChatFormatting;
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

public abstract class CursedObjectItem extends Item {
    public CursedObjectItem(Properties pProperties) {
        super(pProperties);
    }

    public abstract SorcererGrade getGrade();

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(String.format("item.%s.grade", JujutsuKaisen.MOD_ID),
                this.getGrade().getName().copy().withStyle(ChatFormatting.DARK_RED)));
    }
}
