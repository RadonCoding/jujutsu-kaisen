package radon.jujutsu_kaisen.item.cursed_object;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
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
        IJujutsuCapability jujutsuCap = pPlayer.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap != null) {
            ISorcererData data = jujutsuCap.getSorcererData();

            ItemStack stack = pPlayer.getItemInHand(pUsedHand);

            if (data.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                return InteractionResultHolder.fail(stack);
            }

            if (data.hasTrait(Trait.VESSEL) && data.getFingers() == 20) {
                return InteractionResultHolder.fail(stack);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        IJujutsuCapability jujutsuCap = pEntityLiving.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap != null) {
            ISorcererData data = jujutsuCap.getSorcererData();

            if (data.getType() == JujutsuType.CURSE) {
                return super.finishUsingItem(pStack, pLevel, pEntityLiving);
            }

            if (data.hasTrait(Trait.VESSEL)) {
                pStack.shrink(data.addFingers(pStack.getCount()));
                return pStack;
            }
        }
        pStack.shrink(pStack.getCount());
        EntityUtil.convertTo(pEntityLiving, new SukunaEntity(pEntityLiving, pStack.getCount(), false), true, false);
        return ItemStack.EMPTY;
    }
}
