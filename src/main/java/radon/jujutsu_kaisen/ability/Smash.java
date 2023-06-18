package radon.jujutsu_kaisen.ability;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;

import java.util.List;

public class Smash extends Ability {
    private static final float LAUNCH_POWER = 10.0F;
    private static final float EXPLOSIVE_POWER = 1.0F;

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
            owner.level.explode(owner, explosionPos.x(), explosionPos.y(), explosionPos.z(), radius, Level.ExplosionInteraction.NONE);

            float f = radius * 2.0F;
            List<Entity> entities = owner.level.getEntities(owner, new AABB(Mth.floor(explosionPos.x() - (double) f - 1.0D),
                    Mth.floor(explosionPos.y() - (double) f - 1.0D),
                    Mth.floor(explosionPos.z() - (double) f - 1.0D),
                    Mth.floor(explosionPos.x() + (double) f + 1.0D),
                    Mth.floor(explosionPos.y() + (double) f + 1.0D),
                    Mth.floor(explosionPos.z() + (double) f + 1.0D)));

            Vec3 look = owner.getLookAngle();

            for (Entity entity : entities) {
                float distance = entity.distanceTo(owner);
                float scalar = (radius - distance) / radius;
                entity.setDeltaMovement(look.x() * LAUNCH_POWER, look.y() * LAUNCH_POWER, (look.z() * LAUNCH_POWER) * scalar);
            }
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
