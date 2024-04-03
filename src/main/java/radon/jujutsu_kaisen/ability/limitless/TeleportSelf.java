package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class TeleportSelf extends Ability {
    private static final double RANGE = 32.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        return getTarget(owner) instanceof EntityHitResult hit && hit.getEntity() == target && HelperMethods.RANDOM.nextInt(20) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    public static @Nullable HitResult getTarget(LivingEntity owner) {
        HitResult hit = RotationUtil.getLookAtHit(owner, RANGE, target -> !target.isSpectator());
        //if (hit.getType() == HitResult.Type.MISS) return null;
        if (hit.getType() == HitResult.Type.BLOCK && ((BlockHitResult) hit).getDirection() == Direction.DOWN) return null;
        return hit;
    }

    public static void teleport(Entity entity, Vec3 pos) {
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.MASTER, 1.0F, 1.0F);
        entity.setPos(pos.x, pos.y, pos.z);
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.MASTER, 1.0F, 1.0F);
    }

    @Override
    public void run(LivingEntity owner) {
        HitResult target = getTarget(owner);

        if (target != null) {
            owner.swing(InteractionHand.MAIN_HAND);

            teleport(owner, target.getLocation());
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        HitResult target = getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
