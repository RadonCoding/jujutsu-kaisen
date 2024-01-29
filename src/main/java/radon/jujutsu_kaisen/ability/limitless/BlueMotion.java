package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.projectile.BlueProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BlueMotion extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;
        return HelperMethods.RANDOM.nextInt(5) == 0 && owner.hasLineOfSight(target) && owner.distanceTo(target) <= BlueProjectile.RANGE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        BlueProjectile blue = new BlueProjectile(owner, this.getPower(owner), true);
        owner.level().addFreshEntity(blue);

        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.BLUE.get(), SoundSource.MASTER, 1.0F, 1.0F);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.isCooldownDone(JJKAbilities.BLUE_STILL.get()) ? super.isTriggerable(owner) : Status.FAILURE;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.BLUE;
    }
}
