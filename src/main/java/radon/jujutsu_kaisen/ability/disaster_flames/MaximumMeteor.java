package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.effect.MeteorEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MaximumMeteor extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return target != null && owner.distanceTo(target) <= 5.0D && owner.hasLineOfSight(target) && !JJKAbilities.hasToggled(owner, JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get()) &&
                owner.onGround() && (cap.getType() == JujutsuType.CURSE || cap.isUnlocked(JJKAbilities.RCT1.get()) ? owner.getHealth() / owner.getMaxHealth() < 0.9F : owner.getHealth() / owner.getMaxHealth() < 0.4F);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private static boolean canSpawn(LivingEntity owner, float power) {
        Vec3 offset = owner.position().add(0.0D, MeteorEntity.HEIGHT + MeteorEntity.getSize(power), 0.0D);
        BlockHitResult hit = owner.level().clip(new ClipContext(owner.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        return hit.getType() != HitResult.Type.BLOCK;
    }

    @Override
    public void run(LivingEntity owner) {
        if (canSpawn(owner, this.getPower(owner))) {
            owner.swing(InteractionHand.MAIN_HAND);

            MeteorEntity meteor = new MeteorEntity(owner, this.getPower(owner));
            owner.level().addFreshEntity(meteor);
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
        if (!canSpawn(owner, this.getPower(owner))) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }


}
