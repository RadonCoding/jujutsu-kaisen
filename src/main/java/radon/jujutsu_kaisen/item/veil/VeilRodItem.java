package radon.jujutsu_kaisen.item.veil;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;

import java.util.ArrayList;
import java.util.List;

public class VeilRodItem extends BlockItem {
    public VeilRodItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    public static List<Modifier> getModifiers(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);

        if (data.isEmpty()) return List.of();

        return ModifierUtils.getModifiers(data.copyTag());
    }

    public static void setModifier(ItemStack stack, int index, Modifier modifier) {
        CustomData data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        CompoundTag nbt = data.isEmpty() ? new CompoundTag() : data.copyTag();
        ModifierUtils.setModifier(nbt, index, modifier);
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(nbt));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, @NotNull List<Component> pTooltip, @NotNull TooltipFlag pFlag) {
        super.appendHoverText(pStack, pContext, pTooltip, pFlag);

        List<Component> modifiers = new ArrayList<>();
        modifiers.add(CommonComponents.EMPTY);
        modifiers.add(Component.translatable(String.format("item.%s.veil_rod_item.modifiers", JujutsuKaisen.MOD_ID)).withStyle(ChatFormatting.DARK_BLUE));

        boolean success = false;

        for (Modifier modifier : getModifiers(pStack)) {
            if (modifier.getType() == Modifier.Type.NONE) continue;
            modifiers.add(CommonComponents.space().append(modifier.getComponent()));
            success = true;
        }

        if (success) {
            pTooltip.addAll(modifiers);
        }
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pPos, @NotNull Level pLevel, @Nullable Player pPlayer, @NotNull ItemStack pStack, @NotNull BlockState pState) {
        if (pPlayer != null && pLevel.getBlockEntity(pPos) instanceof VeilRodBlockEntity be) {
            be.setOwnerUUID(pPlayer.getUUID());
        }
        return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
    }
}
