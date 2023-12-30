package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffect;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SoulDecimation extends Ability {
    public static final double RANGE = 30.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            if (!owner.canAttack(target)) return null;

            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        MobEffectInstance instance = target.getEffect(JJKEffects.TRANSFIGURED_SOUL.get());

        if (instance == null) return;

        int amplifier = instance.getAmplifier();

        float attackerStrength = IdleTransfiguration.calculateStrength(owner);
        float victimStrength = IdleTransfiguration.calculateStrength(target);

        int required = Math.round((victimStrength / attackerStrength) * 2);

        if (amplifier < required) return;

        target.hurt(JJKDamageSources.soulAttack(owner), target.getMaxHealth());
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
