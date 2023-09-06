package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class DomainAmplification extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, JJKAbilities.MAHORAGA.get())) return false;

        Ability domain = ((ISorcerer) owner).getDomain();
        return target != null && owner.distanceTo(target) < 5.0D && (domain == null || JJKAbilities.hasTrait(owner, Trait.STRONGEST) ||
                !JJKAbilities.hasToggled(owner, domain)) && JJKAbilities.hasToggled(target, JJKAbilities.INFINITY.get());
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level instanceof ServerLevel level) {
            for (int i = 0; i < 8; i++) {
                level.sendParticles(new VaporParticle.VaporParticleOptions(ParticleColors.getCursedEnergyColor(owner), 1.5F, 0.5F, false, 1),
                        owner.getX() + (HelperMethods.RANDOM.nextGaussian() * 0.1D) - owner.getLookAngle().scale(0.3D).x(),
                        owner.getY() + HelperMethods.RANDOM.nextDouble(owner.getBbHeight()),
                        owner.getZ() + (HelperMethods.RANDOM.nextGaussian() * 0.1D) - owner.getLookAngle().scale(0.3D).z(),
                        0, 0.0D, HelperMethods.RANDOM.nextDouble(), 0.0D, 1.5D);
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.1F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.DOMAIN;
    }

    @Override
    public List<Trait> getRequirements() {
        return List.of(Trait.DOMAIN_EXPANSION);
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity owner = event.getEntity();

            if (!JJKAbilities.hasToggled(owner, JJKAbilities.DOMAIN_AMPLIFICATION.get())) return;
            if (!(event.getSource() instanceof JJKDamageSources.JujutsuDamageSource source)) return;

            Ability ability = source.getAbility();

            if (ability == null) return;

            if (ability.isTechnique()) {
                event.setAmount(event.getAmount() * (ability.getRequirements().contains(Trait.REVERSE_CURSED_TECHNIQUE) ? 0.5F : 0.25F));
            }
        }
    }
}
