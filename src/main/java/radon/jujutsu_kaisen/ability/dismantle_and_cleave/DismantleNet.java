package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DismantleNet extends Ability {
    private static final int MIN_SIZE = 4;
    private static final int MAX_SIZE = 8;
    private static final int SIZE = 4;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        int size = Math.max(MIN_SIZE, Math.min(MAX_SIZE, (int) (SIZE * this.getPower(owner))));
        int count = size / 4;

        Vec3 look = owner.getLookAngle();
        Vec3 center = new Vec3(owner.getX(), owner.getEyeY(), owner.getZ()).add(look);

        float power = this.getPower(owner);

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                for (int k = 0; k < count; k++) {
                    double xOffset = (i - (count - 1) / 2.0D) * (double) size / count;
                    double yOffset = (j - (count - 1) / 2.0D) * (double) size / count;
                    double zOffset = (k - (count - 1) / 2.0D) * (double) size / count;

                    Vec3 position = center.add(xOffset, yOffset, zOffset);

                    DismantleProjectile horizontal = new DismantleProjectile(owner, power, 0.0F, position, size);
                    DismantleProjectile vertical = new DismantleProjectile(owner, power, 90.0F, position, size);

                    horizontal.setDeltaMovement(horizontal.getLookAngle().scale(Dismantle.SPEED));
                    vertical.setDeltaMovement(vertical.getLookAngle().scale(Dismantle.SPEED));

                    owner.level().addFreshEntity(horizontal);
                    owner.level().addFreshEntity(vertical);
                }
            }
        }

        if (!owner.level().isClientSide) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }



    @Override
    public boolean isScalable() {
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
