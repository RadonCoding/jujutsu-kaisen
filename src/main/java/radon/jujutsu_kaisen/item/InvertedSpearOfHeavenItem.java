package radon.jujutsu_kaisen.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.util.HelperMethods;

public class InvertedSpearOfHeavenItem extends CursedToolItem {
    public InvertedSpearOfHeavenItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    protected SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return 20;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, LivingEntity pLivingEntity) {
        for (ClosedDomainExpansionEntity domain : HelperMethods.getEntityCollisionsOfClass(ClosedDomainExpansionEntity.class, pLevel, pLivingEntity.getBoundingBox())) {
            domain.discard();
        }
        pStack.hurtAndBreak(2, pLivingEntity, entity -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));

        return pStack;
    }
}
