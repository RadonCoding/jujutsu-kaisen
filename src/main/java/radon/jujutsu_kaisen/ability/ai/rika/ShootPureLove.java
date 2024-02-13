package radon.jujutsu_kaisen.ability.ai.rika;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.effect.PureLoveBeamEntity;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ShootPureLove extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (((RikaEntity) owner).isOpen()) return true;
        if (target == null || target.isDeadOrDying() || !owner.hasLineOfSight(target)) return false;
        return HelperMethods.RANDOM.nextInt(10) == 0;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return owner instanceof RikaEntity && super.isValid(owner);
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        ((RikaEntity) owner).setShooting(PureLoveBeamEntity.CHARGE + PureLoveBeamEntity.DURATION + PureLoveBeamEntity.FRAMES);

        PureLoveBeamEntity beam = new PureLoveBeamEntity(owner, this.getPower(owner));
        owner.level().addFreshEntity(beam);

        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.PURE_LOVE.get(), SoundSource.MASTER, 1.0F, 1.0F);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }
}
