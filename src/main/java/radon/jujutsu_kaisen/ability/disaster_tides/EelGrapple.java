package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
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
import radon.jujutsu_kaisen.entity.effect.CursedEnergyBombEntity;
import radon.jujutsu_kaisen.entity.effect.WaterTorrentEntity;
import radon.jujutsu_kaisen.entity.projectile.EelGrappleProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;


public class EelGrapple extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || !owner.hasLineOfSight(target) || owner.distanceTo(target) > EelGrappleProjectile.RANGE) return false;
        return HelperMethods.RANDOM.nextInt(3) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        EelGrappleProjectile grapple = new EelGrappleProjectile(owner);
        owner.level().addFreshEntity(grapple);
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.WATER;
    }
}
