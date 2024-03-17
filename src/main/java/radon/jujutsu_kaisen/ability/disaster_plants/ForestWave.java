package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.effect.ForestWaveEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ForestWave extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final int DELAY = 3;
    private static final int SPEED = 5;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying() || !owner.hasLineOfSight(target)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.isChanneling(this)) {
            return HelperMethods.RANDOM.nextInt(5) != 0;
        }
        return HelperMethods.RANDOM.nextInt(3) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        int charge = this.getCharge(owner);

        float xRot = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 90.0F;
        float yRot = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 90.0F;

        for (int i = charge < SPEED ? 0 : -SPEED; i <= SPEED; i++) {
            ForestWaveEntity forest = new ForestWaveEntity(owner, this.getOutput(owner));
            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            Vec3 spawn = new Vec3(owner.getX(), owner.getY(), owner.getZ())
                    .add(look.yRot(90.0F).scale(-forest.getBbWidth() * 1.5F))
                    .add(look.scale(charge + i * forest.getBbWidth()));
            forest.moveTo(spawn.x, spawn.y, spawn.z, yRot, xRot);

            if (charge != 0 && owner.level().getEntitiesOfClass(ForestWaveEntity.class, forest.getBoundingBox()).isEmpty())
                continue;

            forest.setDamage(charge >= DELAY);

            owner.level().addFreshEntity(forest);
        }

        for (int i = charge < SPEED ? 0 : -SPEED; i <= SPEED; i++) {
            ForestWaveEntity forest = new ForestWaveEntity(owner, this.getOutput(owner));
            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            Vec3 spawn = new Vec3(owner.getX(), owner.getY(), owner.getZ())
                    .add(look.yRot(90.0F).scale(forest.getBbWidth() * 1.5F))
                    .add(look.scale(charge + i * forest.getBbWidth()));
            forest.moveTo(spawn.x, spawn.y, spawn.z, yRot, xRot);

            if (charge != 0 && owner.level().getEntitiesOfClass(ForestWaveEntity.class, forest.getBoundingBox()).isEmpty())
                continue;

            forest.setDamage(charge >= DELAY);

            owner.level().addFreshEntity(forest);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public int getDuration() {
        return 5;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.PLANTS;
    }
}
