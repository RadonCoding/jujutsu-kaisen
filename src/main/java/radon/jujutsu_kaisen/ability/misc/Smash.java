package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;

public class Smash extends Ability {
    private static final float EXPLOSIVE_POWER = 1.5F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.distanceTo(target) <= 5.0D;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            float radius = EXPLOSIVE_POWER * cap.getGrade().getPower();

            Vec3 explosionPos = new Vec3(owner.getX(), owner.getEyeY() - 0.2D, owner.getZ()).add(owner.getLookAngle());
            owner.level.explode(owner, JJKDamageSources.jujutsuAttack(owner), null, explosionPos, radius, false, Level.ExplosionInteraction.NONE);
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10;
    }

    @Override
    public int getCooldown() {
        return 3 * 20;
    }
}
