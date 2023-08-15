package radon.jujutsu_kaisen.entity.base;

import net.minecraft.world.entity.LivingEntity;

public interface ICommandable {
    boolean canChangeTarget();
    void changeTarget(LivingEntity target);
}
