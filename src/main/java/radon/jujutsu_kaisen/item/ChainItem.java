package radon.jujutsu_kaisen.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.projectile.ChainItemProjectile;

public class ChainItem extends CursedToolItem {
    public ChainItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return 72000;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);

        ItemStack sword = pLivingEntity.getOffhandItem();

        if (sword.getItem() instanceof SwordItem && pRemainingUseDuration == this.getUseDuration(pStack)) {
            if (!(pLivingEntity instanceof Player player && player.getAbilities().instabuild)) {
                sword.hurtAndBreak(1, pLivingEntity, entity -> entity.broadcastBreakEvent(InteractionHand.OFF_HAND));
            }
            ChainItemProjectile projectile = new ChainItemProjectile(pLivingEntity, sword.copy());
            pLevel.addFreshEntity(projectile);

            sword.shrink(1);

            if (sword.isEmpty()) {
                pLivingEntity.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }

            if (!(pLivingEntity instanceof Player player && player.getAbilities().instabuild)) {
                pStack.hurtAndBreak(1, pLivingEntity, entity -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
        }
    }

    @Override
    protected SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }
}
