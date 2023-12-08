package radon.jujutsu_kaisen.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CuriosUtil {
    public static ItemStack findSlot(LivingEntity entity, String identifier) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);

        if (!optional.isPresent()) return ItemStack.EMPTY;

        ICuriosItemHandler inventory = optional.resolve().orElseThrow();

        Optional<SlotResult> result = inventory.findCurio(identifier, 0);

        if (result.isEmpty()) return ItemStack.EMPTY;

        return result.get().stack();
    }

    public static List<ItemStack> findSlots(LivingEntity entity, String... identifiers) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);

        if (!optional.isPresent()) return List.of();

        ICuriosItemHandler inventory = optional.resolve().orElseThrow();

        return inventory.findCurios(identifiers).stream().map(SlotResult::stack).collect(Collectors.toList());
    }

    public static void setItemInSlot(LivingEntity entity, String identifier, ItemStack stack) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);

        if (!optional.isPresent()) return;

        ICuriosItemHandler inventory = optional.resolve().orElseThrow();
        inventory.setEquippedCurio(identifier, 0, stack);
    }
}
