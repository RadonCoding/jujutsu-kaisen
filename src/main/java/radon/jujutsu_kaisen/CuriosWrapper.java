package radon.jujutsu_kaisen;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CuriosWrapper {
    public static List<ItemStack> findSlots(LivingEntity entity, String... identifiers) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);

        if (!optional.isPresent()) return List.of();

        ICuriosItemHandler inventory = optional.resolve().orElseThrow();

        return inventory.findCurios(identifiers).stream().map(SlotResult::stack).collect(Collectors.toList());
    }
}
