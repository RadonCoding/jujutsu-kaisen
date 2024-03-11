package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.Iterator;
import java.util.List;

public class Shuffle extends Ability implements Ability.IChannelened, Ability.IDurationable {
    public static final double RANGE = 30.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying() || !owner.hasLineOfSight(target)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.isChanneling(this)) {
            return HelperMethods.RANDOM.nextInt(10) != 0;
        }
        return this.getTargets(owner).contains(target) && HelperMethods.RANDOM.nextInt(40) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    private List<Entity> getTargets(LivingEntity owner) {
        return owner.level().getEntities(owner, AABB.ofSize(owner.position(), RANGE, RANGE, RANGE), SwapSelf::canSwap);
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (this.getCharge(owner) % 4 != 0) return;

        if (owner.level().isClientSide) return;

        List<Entity> targets = this.getTargets(owner);

        Iterator<Entity> iter = targets.iterator();

        while (iter.hasNext()) {
            Entity first = iter.next();

            iter.remove();

            if (targets.isEmpty()) return;

            Entity second = targets.get(HelperMethods.RANDOM.nextInt(targets.size()));

            SwapSelf.swap(first, second);
        }
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 5.0F;
    }

    @Override
    public int getDuration() {
        return 20;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        List<Entity> targets = this.getTargets(owner);

        if (targets.size() < 2) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        List<Entity> targets = this.getTargets(owner);

        if (targets.size() < 2) {
            return Status.FAILURE;
        }
        return super.isStillUsable(owner);
    }
}
