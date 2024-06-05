package radon.jujutsu_kaisen.util;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CuriosUtil {
    public static ItemStack findSlot(LivingEntity entity, String identifier) {
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);

        if (optional.isEmpty()) return ItemStack.EMPTY;

        ICuriosItemHandler inventory = optional.get();

        Optional<SlotResult> result = inventory.findCurio(identifier, 0);

        if (result.isEmpty()) return ItemStack.EMPTY;

        return result.get().stack();
    }

    public static List<ItemStack> findSlots(LivingEntity entity, String... identifiers) {
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);

        if (optional.isEmpty()) return List.of();

        ICuriosItemHandler inventory = optional.get();

        return inventory.findCurios(identifiers).stream().map(SlotResult::stack).collect(Collectors.toList());
    }

    public static void setItemInSlot(LivingEntity entity, String identifier, ItemStack stack) {
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);

        if (optional.isEmpty()) return;

        ICuriosItemHandler inventory = optional.get();

        inventory.setEquippedCurio(identifier, 0, stack);
    }
}
