package radon.jujutsu_kaisen.entity.base;

import net.minecraft.world.entity.LivingEntity;

public interface ICommandable {
    boolean changeTarget(LivingEntity target);
}
