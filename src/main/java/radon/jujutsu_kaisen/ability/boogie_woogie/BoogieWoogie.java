package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BoogieWoogie extends Ability {
    public static final double RANGE = 30.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHitAny(owner, RANGE) instanceof EntityHitResult hit) {
            Entity target = hit.getEntity();
            return target.isPickable() || target instanceof ItemEntity ? target : null;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target != null) {
            owner.level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
            owner.level.playSound(null, target.getX(), target.getY(), target.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 1.0F, 1.0F);

            Vec3 pos = target.position();
            target.setPos(owner.position());
            owner.setPos(pos);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }
}
