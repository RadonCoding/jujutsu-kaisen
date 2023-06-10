package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.BlueProjectile;

public class Blue extends Ability {
    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        BlueProjectile blue = new BlueProjectile(owner);
        owner.level.addFreshEntity(blue);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }
}
