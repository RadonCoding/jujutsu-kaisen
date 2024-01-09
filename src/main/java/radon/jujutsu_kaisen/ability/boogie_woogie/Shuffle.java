package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.effect.CursedEnergyImbuedItem;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Shuffle extends Ability implements Ability.IChannelened {
    public static final double RANGE = 30.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || !owner.hasLineOfSight(target)) return false;

        if (JJKAbilities.isChanneling(owner, this)) {
            return HelperMethods.RANDOM.nextInt(5) != 0;
        }
        return this.getTargets(owner).contains(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private List<Entity> getTargets(LivingEntity owner) {
        return owner.level().getEntities(owner, AABB.ofSize(owner.position(), RANGE, RANGE, RANGE), SwapSelf::canSwap);
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        List<Entity> targets = this.getTargets(owner);

        Iterator<Entity> iter = targets.iterator();

        while (iter.hasNext()) {
            Entity first = iter.next();

            iter.remove();

            if (targets.isEmpty()) return;

            Entity second = targets.get(HelperMethods.RANDOM.nextInt(targets.size()));

            second.level().playSound(null, second.getX(), second.getY(), second.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
            second.level().playSound(null, first.getX(), first.getY(), first.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 1.0F, 1.0F);

            Vec3 pos = first.position();

            Vec2 ownerRot = second.getRotationVector();
            Vec2 targetRot = first.getRotationVector();

            first.teleportTo(second.getX(), second.getY(), second.getZ());
            second.teleportTo(pos.x, pos.y, pos.z);

            first.setYRot(ownerRot.y);
            first.setXRot(ownerRot.x);

            second.setYRot(targetRot.y);
            second.setXRot(targetRot.x);
        }
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.MELEE;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 5.0F;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        List<Entity> targets = this.getTargets(owner);

        if (targets.size() < 2) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }
}
