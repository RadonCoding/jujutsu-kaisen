package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.item.DisplayCaseItem;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        ItemEntity entity = (ItemEntity) (Object) this;
        ItemStack stack = entity.getItem();
        Item item = stack.getItem();

        if (item instanceof DisplayCaseItem) {
            ((DisplayCaseItem) item).updateDisplayCase(entity.level, stack);
        }
    }
}
