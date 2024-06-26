package radon.jujutsu_kaisen.mixin.common;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.util.CuriosUtil;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"), cancellable = true)
    private static void getEnchantmentLevel(Enchantment pEnchantment, LivingEntity pEntity, CallbackInfoReturnable<Integer> cir) {
        List<ItemStack> result = CuriosUtil.findSlots(pEntity, "right_hand", "left_hand");

        int i = cir.getReturnValue();

        for (ItemStack stack : result) {
            int j = stack.getEnchantmentLevel(pEnchantment);

            if (j > i) {
                i = j;
            }
        }
        cir.setReturnValue(i);
    }
}
