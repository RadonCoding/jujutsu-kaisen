package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.entity.effect.MeteorEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MaximumMeteor extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return !JJKAbilities.hasToggled(owner, JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get()) && owner.getHealth() / owner.getMaxHealth() <= 0.5F &&
                HelperMethods.RANDOM.nextInt(10) == 0 && target != null && owner.distanceTo(target) <= 5.0D && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private boolean canSpawn(LivingEntity owner) {
        Vec3 offset = owner.position().add(0.0D, MeteorEntity.HEIGHT + MeteorEntity.SIZE, 0.0D);
        BlockHitResult hit = owner.level.clip(new ClipContext(owner.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        return hit.getType() != HitResult.Type.BLOCK;
    }

    @Override
    public void run(LivingEntity owner) {
        if (this.canSpawn(owner)) {
            owner.swing(InteractionHand.MAIN_HAND);

            MeteorEntity meteor = new MeteorEntity(owner);
            owner.level.addFreshEntity(meteor);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1000.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        if (!this.canSpawn(owner)) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
