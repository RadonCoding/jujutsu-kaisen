package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;

public interface ICharged extends IChanneled {
    default boolean onRelease(LivingEntity owner) {
            return true;
        }
}
