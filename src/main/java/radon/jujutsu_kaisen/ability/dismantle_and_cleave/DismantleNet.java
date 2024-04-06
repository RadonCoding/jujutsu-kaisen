package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class DismantleNet extends Ability {
    private static final int MIN_SIZE = 8;
    private static final int MAX_SIZE = 32;
    private static final int SIZE = 4;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;
        return HelperMethods.RANDOM.nextInt(20) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        int size = Math.max(MIN_SIZE, Math.min(MAX_SIZE, (int) (SIZE * this.getOutput(owner))));
        int count = size / 4;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        Vec3 center = new Vec3(owner.getX(), owner.getEyeY(), owner.getZ()).add(look);

        float power = this.getOutput(owner);

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                double xOffset = (i - (count - 1) / 2.0D) * (double) size / count;
                double yOffset = (j - (count - 1) / 2.0D) * (double) size / count;

                Vec3 xAxis = owner.getUpVector(1.0F);
                Vec3 yAxis = look.cross(xAxis).normalize();

                Vec3 position = center.add(xAxis.scale(xOffset)).add(yAxis.scale(yOffset));

                DismantleProjectile horizontal = new DismantleProjectile(owner, power, 0.0F, position, size);
                DismantleProjectile vertical = new DismantleProjectile(owner, power, 90.0F, position, size);

                horizontal.setDeltaMovement(look.scale(Dismantle.SPEED));
                vertical.setDeltaMovement(look.scale(Dismantle.SPEED));

                owner.level().addFreshEntity(horizontal);
                owner.level().addFreshEntity(vertical);
            }
        }

        if (!owner.level().isClientSide) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 150.0F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
