package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.RedProjectile;

public class Red extends Ability {
    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void runClient(LivingEntity entity) {

    }

    @Override
    public void runServer(LivingEntity entity) {
        Vec3 look = entity.getLookAngle();
        RedProjectile fireball = new RedProjectile(entity, look.x(), look.y(), look.z());
        entity.level.addFreshEntity(fireball);
    }

    @Override
    public float getCost() {
        return 100.0F;
    }
}
