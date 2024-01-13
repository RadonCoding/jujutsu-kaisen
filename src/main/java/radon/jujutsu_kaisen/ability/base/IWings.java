package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IWings {
    default boolean checkFlight(LivingEntity entity) {
        return false;
    }
}
