package radon.jujutsu_kaisen.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.cursed_tool.ICursedTool;

import java.util.List;

public abstract class CursedToolItem extends SwordItem implements ICursedTool {
    public CursedToolItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    public abstract SorcererGrade getGrade();

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        MutableComponent name = super.getName(pStack).copy();
        return name.withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);

        pTooltipComponents.add(Component.translatable(String.format("%s.desc", this.getDescriptionId())));
        pTooltipComponents.add(Component.translatable(String.format("item.%s.grade", JujutsuKaisen.MOD_ID),
                this.getGrade().getName().copy().withStyle(ChatFormatting.DARK_RED)));
    }
}
