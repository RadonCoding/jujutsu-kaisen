package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.effect.ForestDashEntity;
import radon.jujutsu_kaisen.entity.effect.ForestWaveEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ForestDash extends Ability implements Ability.IChannelened {
    private static final double SPEED = 2.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return JJKAbilities.isChanneling(owner, this) ? target != null : HelperMethods.RANDOM.nextInt(3) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        Vec3 look = owner.getLookAngle();

        Vec3 start = owner.position().subtract(owner.getUpVector(1.0F).scale(ForestDashEntity.SIZE));

        for (double i = 0.0D; i <= SPEED * 2; i += ForestDashEntity.SIZE / 2) {
            Vec3 offset = start.add(look.scale(i));

            ForestDashEntity forest = new ForestDashEntity(owner);
            forest.moveTo(offset.x(), offset.y(), offset.z(), 180.0F - owner.getYRot(), owner.getXRot());
            owner.level().addFreshEntity(forest);
        }
        owner.setDeltaMovement(look.scale(SPEED));
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {

    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
