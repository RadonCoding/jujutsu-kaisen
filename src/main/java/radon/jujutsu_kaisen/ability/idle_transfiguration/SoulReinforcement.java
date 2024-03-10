package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.DamageUtil;
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
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (victimCap == null) return;

            ISorcererData victimSorcererData = victimCap.getSorcererData();
            IAbilityData victimAbilityData = victimCap.getAbilityData();

            if (!victimAbilityData.hasToggled(JJKAbilities.SOUL_REINFORCEMENT.get())) return;

            if (DamageUtil.isMelee(source)) {
                if (source.getEntity() instanceof LivingEntity attacker) {
                    IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

                    if (attackerCap != null) {
                        ISorcererData data = attackerCap.getSorcererData();

                        if (data.hasTrait(Trait.VESSEL)) {
                            return;
                        }
                    }
                }
            }

            if (!DamageUtil.isBlockable(victim, source)) return;

            if (source.is(JJKDamageSources.SOUL)) return;

            float cost = event.getAmount() * 2.0F * (victimSorcererData.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);
            if (victimSorcererData.getEnergy() < cost) return;
            victimSorcererData.useEnergy(cost);

            int count = 8 + (int) (victim.getBbWidth() * victim.getBbHeight()) * 16;

            for (int i = 0; i < count; i++) {
                double x = victim.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (victim.getBbWidth() * 2) - victim.getLookAngle().scale(0.35D).x;
                double y = victim.getY() + HelperMethods.RANDOM.nextDouble() * victim.getBbHeight();
                double z = victim.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (victim.getBbWidth() * 2) - victim.getLookAngle().scale(0.35D).z;
                ((ServerLevel) victim.level()).sendParticles(ParticleTypes.SOUL, x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble() * 0.1D, 0.0D, 1.0D);
            }

            if (victim instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimSorcererData.serializeNBT()), player);
            }
            event.setCanceled(true);
        }
    }
}