package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CursedEnergyFlow extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return (target != null && owner.distanceTo(target) <= 10.0D) ||
                !owner.level.getEntities(owner, owner.getBoundingBox().inflate(3.0D),
                        entity -> entity instanceof Projectile projectile && projectile.getOwner() != owner && projectile.getDeltaMovement().lengthSqr() >= 1.0E-7D).isEmpty();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level instanceof ServerLevel level)) return;

        double width = owner.getBbWidth();
        double height = owner.getBbHeight();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (int i = 0; i < 8; i++) {
                double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width - owner.getLookAngle().scale(0.35D).x();
                double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * height;
                double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width - owner.getLookAngle().scale(0.35D).z();
                level.sendParticles(new VaporParticle.VaporParticleOptions(ParticleColors.getCursedEnergyColor(owner), (float) width * 3.0F, 0.5F, true, 1),
                        x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble(), 0.0D, 1.5D);
            }

            if (cap.getNature() == CursedEnergyNature.LIGHTNING) {
                for (int i = 0; i < 2; i++) {
                    double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width;
                    double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * height;
                    double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width;
                    level.sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColor(owner), 0.2F),
                            x, y, z, 0, 0.0D, 0, 0.0D, 0.0D);
                }

                if (HelperMethods.RANDOM.nextInt(50) == 0) {
                    owner.level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 1.0F, 0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F);
                }
            }
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.3F;
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
        public static void onLivingHurt(LivingHurtEvent event) {
            // Damage
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            boolean melee = !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK));

            if (JJKAbilities.hasToggled(attacker, JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (melee) {
                        switch (attackerCap.getNature()) {
                            case BASIC -> event.setAmount(event.getAmount() * 1.25F);
                            case LIGHTNING -> event.setAmount(event.getAmount() * 1.25F * (attacker.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.NYOI_STAFF.get()) ? 2.0F : 1.0F));
                            case ROUGH -> event.setAmount(event.getAmount() * 1.5F);
                        }
                    }
                }
            }

            LivingEntity victim = event.getEntity();

            if (melee) {
                if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (JJKAbilities.hasToggled(attacker, JJKAbilities.CURSED_ENERGY_FLOW.get())) {

                        if (attackerCap.getNature() == CursedEnergyNature.LIGHTNING) {
                            victim.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 20 * (attacker.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.NYOI_STAFF.get()) ? 2 : 1), 0, false, false, false));
                            victim.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);

                            if (!attacker.level.isClientSide) {
                                for (int i = 0; i < 8; i++) {
                                    double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                                    double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                                    double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                                    ((ServerLevel) attacker.level).sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColor(attacker), 0.5F),
                                            victim.getX() + offsetX, victim.getY() + offsetY, victim.getZ() + offsetZ,
                                            0, 0.0D, 0.0D, 0.0D, 0.0D);
                                }
                            }
                        }
                    }
                }
            }

            // Shield
            if (JJKAbilities.hasToggled(victim, JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    event.setAmount(event.getAmount() * 0.9F);

                    switch (cap.getNature()) {
                        case LIGHTNING -> attacker.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 20, 0,
                                false, false, false));
                        case ROUGH -> {
                            if (ThornsEnchantment.shouldHit(3, victim.getRandom())) {
                                attacker.hurt(JJKDamageSources.jujutsuAttack(victim, null), (float) ThornsEnchantment.getDamage(3, victim.getRandom()));
                            }
                        }
                    }
                }
            }
        }
    }
}
