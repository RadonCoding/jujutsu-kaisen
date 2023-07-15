package radon.jujutsu_kaisen.ability.yuta;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.entity.projectile.PureLoveProjectile;

public class PureLove extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (!(owner instanceof RikaEntity rika)) return false;
        return rika.isOpen();
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        PureLoveProjectile purple = new PureLoveProjectile(owner);
        owner.level.addFreshEntity(purple);
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        if (!(owner instanceof RikaEntity rika)) return Status.FAILURE;
        if (!rika.isOpen()) return Status.FAILURE;
        return super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }
}
