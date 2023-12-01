package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.CuriosWrapper;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.event.CuriosEventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"), cancellable = true)
    private static void getEnchantmentLevel(Enchantment pEnchantment, LivingEntity pEntity, CallbackInfoReturnable<Integer> cir) {
        List<ItemStack> result = new ArrayList<>();

        if (ModList.get().isLoaded(JujutsuKaisen.CURIOS_MOD_ID)) {
            result.addAll(CuriosWrapper.findSlots(pEntity, "right_hand", "left_hand"));
        }

        int i = cir.getReturnValue();

        for(ItemStack itemstack : result) {
            int j = EnchantmentHelper.getTagEnchantmentLevel(pEnchantment, itemstack);

            if (j > i) {
                i = j;
            }
        }
        cir.setReturnValue(i);
    }
}
