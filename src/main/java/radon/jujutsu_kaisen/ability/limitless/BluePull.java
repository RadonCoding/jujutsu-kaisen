package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.projectile.BlueProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class BluePull extends Ability {
    private static final double RANGE = 32.0D;
    private static final float POWER = 3.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying() || !owner.hasLineOfSight(target) || owner.distanceTo(target) > BlueProjectile.RANGE) return false;
        return HelperMethods.RANDOM.nextInt(40) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE, target -> !target.isSpectator()) instanceof EntityHitResult hit) {
            return hit.getEntity();
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        Entity target = this.getTarget(owner);

        if (target == null) return;

        Vec3 direction = owner.position().subtract(target.position());
        Vec3 balanced = direction.scale(POWER / direction.length());
        target.push(balanced.x, balanced.y, balanced.z);
        target.move(MoverType.SELF, new Vec3(0.0D, 1.1999999F, 0.0D));
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Status.FAILURE;

        IAbilityData data = cap.getAbilityData();
        return data.isCooldownDone(JJKAbilities.BLUE_MOTION.get()) ? super.isTriggerable(owner) : Status.FAILURE;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 25.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
