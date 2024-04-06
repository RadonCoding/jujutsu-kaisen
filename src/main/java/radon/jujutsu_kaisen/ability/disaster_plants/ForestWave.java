package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.effect.ForestWaveEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ForestWave extends Ability {
    private static final int RANGE = 64;
    private static final int SPEED = 8;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;
        return HelperMethods.RANDOM.nextInt(3) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        Vec3 spawn = new Vec3(owner.getX(), owner.getY(), owner.getZ());
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        for (int i = -3; i < RANGE / SPEED; i++) {
            int current = i * SPEED;

            data.delayTickEvent(() -> {
                for (int j = 0; j < SPEED; j++) {
                    float xRot = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 90.0F;
                    float yRot = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 90.0F;

                    Vec3 forward = RotationUtil.calculateViewVector(owner.getXRot(), owner.getYRot());
                    Vec3 up = RotationUtil.calculateViewVector(owner.getXRot() - 90.0F, owner.getYRot());

                    Vec3 side = forward.cross(up);

                    ForestWaveEntity forest = new ForestWaveEntity(owner, this.getOutput(owner));

                    Vec3 offset = spawn
                            .add(side.scale(-forest.getBbWidth() * 2.0F))
                            .add(look.scale((current + j) * forest.getBbWidth()));
                    forest.moveTo(offset.x, offset.y, offset.z, yRot, xRot);
                    owner.level().addFreshEntity(forest);
                }
            }, i + 3);
        }

        for (int i = -3; i < RANGE / SPEED; i++) {
            int current = i * SPEED;

            data.delayTickEvent(() -> {
                for (int j = 0; j < SPEED; j++) {
                    float xRot = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 90.0F;
                    float yRot = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 90.0F;

                    Vec3 forward = RotationUtil.calculateViewVector(owner.getXRot(), owner.getYRot());
                    Vec3 up = RotationUtil.calculateViewVector(owner.getXRot() - 90.0F, owner.getYRot());

                    Vec3 side = forward.cross(up);
                    ForestWaveEntity forest = new ForestWaveEntity(owner, this.getOutput(owner));

                    Vec3 offset = spawn
                            .add(side.scale(forest.getBbWidth() * 2.0F))
                            .add(look.scale((current + j) * forest.getBbWidth()));
                    forest.moveTo(offset.x, offset.y, offset.z, yRot, xRot);
                    owner.level().addFreshEntity(forest);
                }
            }, i + 3);
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
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.PLANTS;
    }
}
