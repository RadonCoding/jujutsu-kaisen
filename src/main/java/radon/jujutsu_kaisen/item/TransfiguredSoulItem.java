package radon.jujutsu_kaisen.item;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.idle_transfiguration.IIdleTransfigurationData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;

public class TransfiguredSoulItem extends Item {
    public TransfiguredSoulItem(Properties pProperties) {
        super(pProperties);
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        IJujutsuCapability cap = pEntityLiving.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return stack;

        IIdleTransfigurationData data = cap.getIdleTransfigurationData();

        data.increaseTransfiguredSouls();

        return stack;
    }
}
