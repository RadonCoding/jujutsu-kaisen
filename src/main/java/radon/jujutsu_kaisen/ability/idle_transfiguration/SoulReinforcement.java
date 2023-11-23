package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SoulReinforcement extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (!JJKAbilities.hasToggled(victim, JJKAbilities.SOUL_REINFORCEMENT.get())) return;

            if (source.getEntity() instanceof LivingEntity attacker) {
                boolean melee = (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource src && src.getAbility() != null && src.getAbility().isMelee())
                        || !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SOUL));

                if (melee) {
                    if (JJKAbilities.hasToggled(attacker, JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                        return;
                    }
                }
            }

            if (source.is(JJKDamageSources.SOUL)) return;

            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            for (DomainExpansionEntity domain : cap.getDomains(((ServerLevel) victim.level()))) {
                if (domain.getOwner() == source.getEntity()) return;
            }

            int count = 8 + (int) (victim.getBbWidth() * victim.getBbHeight()) * 16;

            for (int i = 0; i < count; i++) {
                double x = victim.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (victim.getBbWidth() * 2) - victim.getLookAngle().scale(0.35D).x();
                double y = victim.getY() + HelperMethods.RANDOM.nextDouble() * victim.getBbHeight();
                double z = victim.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (victim.getBbWidth() * 2) - victim.getLookAngle().scale(0.35D).z();
                ((ServerLevel) victim.level()).sendParticles(ParticleTypes.SOUL, x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble() * 0.1D, 0.0D, 1.0D);
            }
            event.setCanceled(true);
        }
    }
}