package radon.jujutsu_kaisen.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;

public class TransfiguredSoulItem extends Item {
    public TransfiguredSoulItem(Properties pProperties) {
        super(pProperties);
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        ISorcererData data = pEntityLiving.getData(JJKAttachmentTypes.SORCERER);

        if (data != null) {
            data.increaseTransfiguredSouls();
        }
        return stack;
    }
}
