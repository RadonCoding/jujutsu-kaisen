package radon.jujutsu_kaisen.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.item.base.CursedToolItem;

public class InvertedSpearOfHeavenItem extends CursedToolItem {
    public static final int COOLDOWN = 3 * 60 * 20;

    public InvertedSpearOfHeavenItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        if (isOnCooldown(pLevel.getGameTime(), pStack)) {
            if (pLevel.isClientSide) {
                ClientWrapper.setOverlayMessage(Component.translatable(String.format("chat.%s.cooldown", JujutsuKaisen.MOD_ID),
                        getCooldownInSeconds(pLevel.getGameTime(), pStack)));
            }
        }
    }

    @Override
    protected SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    public static int getCooldownInSeconds(long current, ItemStack stack) {
        long timestamp = stack.getOrCreateTag().getLong("timestamp");
        return (COOLDOWN - (int) (current - timestamp)) / 20;
    }

    public static boolean isOnCooldown(long current, ItemStack stack) {
        long timestamp = stack.getOrCreateTag().getLong("timestamp");
        return current - timestamp < COOLDOWN;
    }

    public static void setCooldown(ItemStack stack, long current) {
        stack.getOrCreateTag().putLong("timestamp", current);
    }
}
