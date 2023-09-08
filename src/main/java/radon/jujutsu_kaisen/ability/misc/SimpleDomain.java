package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class SimpleDomain extends Ability implements Ability.IToggled, Ability.IDurationable {
    private static final double X_STEP = 0.05D;
    public static final double RADIUS = 3.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (!owner.level.isClientSide) {
            if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            for (DomainExpansionEntity domain : cap.getDomains((ServerLevel) owner.level)) {
                if (!domain.hasSureHitEffect() || !domain.checkSureHitEffect()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 2, 0, false, false, false));

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            float factor = (float) cap.getRemaining(this) / (float) this.getDuration();

            if (!owner.level.isClientSide) {
                ParticleOptions particle = new VaporParticle.VaporParticleOptions(ParticleColors.SIMPLE_DOMAIN, HelperMethods.RANDOM.nextFloat() * 1.5F,
                        1.0F, true, 1);

                for (double phi = 0.0D; phi < Math.PI * factor; phi += X_STEP) {
                    double x = owner.getX() + RADIUS * Math.cos(phi);
                    double y = owner.getY();
                    double z = owner.getZ() + RADIUS * Math.sin(phi);

                    ((ServerLevel) owner.level).sendParticles(particle, x, y, z, 0,
                            0.0D,
                            HelperMethods.RANDOM.nextDouble(),
                            0.0D,
                            1.0D);
                }
            }
        });
    }

    @Override
    public int getDuration() {
        return 5 * 20;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
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

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.DOMAIN;
    }

    @Override
    public List<Trait> getRequirements() {
        return List.of(Trait.SIMPLE_DOMAIN);
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) {
                    cap.toggle(victim, JJKAbilities.SIMPLE_DOMAIN.get());

                    if (victim instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                    }
                }
            });
        }
    }
}
