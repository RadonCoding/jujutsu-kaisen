package radon.jujutsu_kaisen.ability.disaster_plants;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.effect.ForestDashEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ForestDash extends Ability implements IChanneled {
    private static final double SPEED = 2.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target) || owner.distanceTo(target) < 3.0D) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        return data.isChanneling(this) || HelperMethods.RANDOM.nextInt(20) == 0;
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        Vec3 start = owner.position().subtract(owner.getUpVector(1.0F).scale(ForestDashEntity.SIZE * 1.25D));

        for (double i = 0.0D; i <= SPEED * 2; i += ForestDashEntity.SIZE) {
            Vec3 offset = start.add(look.scale(i));

            ForestDashEntity forest = new ForestDashEntity(owner);
            forest.moveTo(offset.x(), offset.y(), offset.z(), 180.0F - RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));
            owner.level().addFreshEntity(forest);
        }
        owner.setDeltaMovement(look.scale(SPEED));
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
