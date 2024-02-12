package radon.jujutsu_kaisen.item.cursed_tool;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class MimicryKatanaItem extends KatanaItem {
    public MimicryKatanaItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        return false;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        ISorcererData data = pEntity.getData(JJKAttachmentTypes.SORCERER);

        if (!data.hasSummonOfClass(DomainExpansionEntity.class)) {
            pStack.shrink(1);
        }
    }
}