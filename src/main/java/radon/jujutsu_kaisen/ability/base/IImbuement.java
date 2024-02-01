package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IImbuement {
    void hit(ItemStack stack, LivingEntity owner, LivingEntity target);
}
