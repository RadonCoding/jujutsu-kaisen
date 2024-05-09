package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.util.VeilUtil;

import java.util.List;

public interface IVeil extends IBarrier {
    List<Modifier> getModifiers();

    default boolean isAllowed(Entity entity) {
        LivingEntity owner = this.getOwner();

        if (owner == null) return true;

        return VeilUtil.isAllowed(entity, owner.getUUID(), this.getModifiers());
    }

    default boolean canDamage(LivingEntity victim) {
        LivingEntity owner = this.getOwner();

        if (owner == null) return true;

        return VeilUtil.canDamage(this.getModifiers());
    }

    default boolean canDestroy(Entity entity, BlockPos target) {
        return VeilUtil.canDestroy(entity, target, ((Entity) this).getUUID(), this.getModifiers());
    }
}
