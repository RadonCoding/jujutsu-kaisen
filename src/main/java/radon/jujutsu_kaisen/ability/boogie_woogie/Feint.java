package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.sound.JJKSounds;

public class Feint extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
