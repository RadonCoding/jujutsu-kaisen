package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SoulReinforcement extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable(LivingEntity owner) {
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
        return 0;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class SoulReinforcementForgeEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!victimCap.hasToggled(JJKAbilities.SOUL_REINFORCEMENT.get())) return;

            if (source.getEntity() instanceof LivingEntity attacker) {
                if (!attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
                ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                if (HelperMethods.isMelee(source)) {
                    if ((attackerCap.hasTrait(Trait.VESSEL) && attackerCap.getFingers() > 0) || attackerCap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                        return;
                    }
                }
            }

            if (source.is(JJKDamageSources.SOUL) || (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu && jujutsu.getAbility() == JJKAbilities.OUTPUT_RCT.get())) return;

            for (DomainExpansionEntity domain : VeilHandler.getDomains(((ServerLevel) victim.level()), victim.blockPosition())) {
                if (domain.getOwner() == source.getEntity()) return;
            }

            float cost = event.getAmount() * 2.0F * (victimCap.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);
            if (victimCap.getEnergy() < cost) return;
            victimCap.useEnergy(cost);

            int count = 8 + (int) (victim.getBbWidth() * victim.getBbHeight()) * 16;

            for (int i = 0; i < count; i++) {
                double x = victim.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (victim.getBbWidth() * 2) - victim.getLookAngle().scale(0.35D).x;
                double y = victim.getY() + HelperMethods.RANDOM.nextDouble() * victim.getBbHeight();
                double z = victim.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (victim.getBbWidth() * 2) - victim.getLookAngle().scale(0.35D).z;
                ((ServerLevel) victim.level()).sendParticles(ParticleTypes.SOUL, x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble() * 0.1D, 0.0D, 1.0D);
            }

            if (victim instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
            }
            event.setCanceled(true);
        }
    }
}