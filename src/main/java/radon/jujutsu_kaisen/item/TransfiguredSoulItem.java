package radon.jujutsu_kaisen.item;

import com.google.errorprone.annotations.Var;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

public class TransfiguredSoulItem extends Item {
    public TransfiguredSoulItem(Properties pProperties) {
        super(pProperties);
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        if (pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.increaseTransfiguredSouls();
        }
        return stack;
    }
}
