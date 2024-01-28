package radon.jujutsu_kaisen.item.cursed_object;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;
import radon.jujutsu_kaisen.util.EntityUtil;

public class SukunaFingerItem extends CursedObjectItem {
    public SukunaFingerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (pPlayer.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = pPlayer.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                return InteractionResultHolder.fail(stack);
            }

            if (cap.hasTrait(Trait.VESSEL) && cap.getFingers() == 20) {
                return InteractionResultHolder.fail(stack);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        if (pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getType() == JujutsuType.CURSE) {
                return super.finishUsingItem(pStack, pLevel, pEntityLiving);
            }

            if (cap.hasTrait(Trait.VESSEL)) {
                pStack.shrink(cap.addFingers(pStack.getCount()));
                return pStack;
            }
        }
        pStack.shrink(pStack.getCount());
        EntityUtil.convertTo(pEntityLiving, new SukunaEntity(pEntityLiving, pStack.getCount(), false), true, false);
        return ItemStack.EMPTY;
    }
}
