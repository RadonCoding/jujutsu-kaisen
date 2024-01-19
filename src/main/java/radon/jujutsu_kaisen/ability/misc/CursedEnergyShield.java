package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class CursedEnergyShield extends Ability implements Ability.IChannelened {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.NONE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        if (owner instanceof Player) {
            for (int i = 0; i < 18; i++) {
                double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2.25F) - RotationUtil.getTargetAdjustedLookAngle(owner).scale(0.35D).x;
                double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
                double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2.25F) - RotationUtil.getTargetAdjustedLookAngle(owner).scale(0.35D).z;
                double speed = (owner.getBbHeight() * 0.45F) * HelperMethods.RANDOM.nextDouble();
                level.sendParticles(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.getCursedEnergyColor(owner), owner.getBbWidth() * 0.75F,
                        0.2F, 6), x, y, z, 0, 0.0D, speed, 0.0D, 1.0D);
            }
        }
        owner.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 2, 1, false, false, false));
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return JJKAbilities.hasToggled(owner, JJKAbilities.CURSED_ENERGY_FLOW.get()) && super.isValid(owner);
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 3.0F;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return true;
    }
}
