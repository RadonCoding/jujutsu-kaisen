package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Teleport extends Ability {
    private static final double RANGE = 100.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.hasLineOfSight(target) && this.getTarget(owner) instanceof EntityHitResult hit && hit.getEntity() == target;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable HitResult getTarget(LivingEntity owner) {
        HitResult hit = HelperMethods.getLookAtHit(owner, RANGE);
        if (hit.getType() == HitResult.Type.MISS) return null;
        if (hit.getType() == HitResult.Type.BLOCK && (owner.level().getBlockState(((BlockHitResult) hit).getBlockPos().above()).canOcclude() ||
                ((BlockHitResult) hit).getDirection() != Direction.UP)) return null;
        return hit;
    }

    @Override
    public void run(LivingEntity owner) {
        HitResult target = this.getTarget(owner);

        if (target != null) {
            owner.playSound(SoundEvents.ENDERMAN_TELEPORT);

            owner.swing(InteractionHand.MAIN_HAND);

            Vec3 pos = target.getLocation();
            owner.setPos(pos.x(), pos.y(), pos.z());
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        HitResult target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
