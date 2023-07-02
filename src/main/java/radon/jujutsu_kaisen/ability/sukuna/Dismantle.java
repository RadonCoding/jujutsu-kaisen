package radon.jujutsu_kaisen.ability.sukuna;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;

public class Dismantle extends Ability {
    private static final float SPEED = 5.0F;

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        DismantleProjectile dismantle = new DismantleProjectile(owner);
        owner.level.addFreshEntity(dismantle);

        dismantle.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, SPEED, 1.0F);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
