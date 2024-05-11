package radon.jujutsu_kaisen.ability.boogie_woogie;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.IAttack;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.ICharged;
import radon.jujutsu_kaisen.ability.IDomainAttack;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.IToggled;
import radon.jujutsu_kaisen.entity.projectile.CursedEnergyImbuedItemProjectile;

public class CEThrow extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

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
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        ItemStack stack = owner.getItemInHand(InteractionHand.MAIN_HAND);

        CursedEnergyImbuedItemProjectile item = new CursedEnergyImbuedItemProjectile(owner, stack.copyWithCount(1));
        owner.level().addFreshEntity(item);

        stack.shrink(1);

        SwapOthers.setTarget(owner, item);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (owner.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }
}
