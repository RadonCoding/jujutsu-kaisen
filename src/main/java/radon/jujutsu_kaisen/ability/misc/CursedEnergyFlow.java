package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JujutsuLightningEntity;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CursedEnergyFlow extends Ability implements Ability.IToggled {
    private static final float LIGHTNING_DAMAGE = 5.0F;

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (int i = 0; i < 4; i++) {
            double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 1.25F) - owner.getLookAngle().scale(0.35D).x();
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * (owner.getBbHeight());
            double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 1.25F) - owner.getLookAngle().scale(0.35D).z();
            double speed = (owner.getBbHeight() * 0.1F) * HelperMethods.RANDOM.nextDouble();
            level.sendParticles(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.getCursedEnergyColor(owner), owner.getBbWidth() * 0.5F,
                            0.2F, 16), x, y, z, 0, 0.0D, speed, 0.0D, 1.0D);
        }

        if (cap.getNature() == CursedEnergyNature.LIGHTNING) {
            for (int i = 0; i < 4; i++) {
                double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2);
                double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * (owner.getBbHeight() * 1.25F);
                double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2);
                level.sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1),
                        x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            if (owner.isInWater()) {
                for (Entity entity : owner.level().getEntities(owner, owner.getBoundingBox().inflate(16.0D))) {
                    if (!entity.isInWater()) continue;

                    if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), LIGHTNING_DAMAGE * this.getPower(owner))) {
                        for (int i = 0; i < 16; i++) {
                            double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                            double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * (entity.getBbHeight() * 1.25F);
                            double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                            level.sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1),
                                    x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
                        }
                        owner.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        float cost = 0.2F;

        if (owner.isInWater() && cap.getNature() == CursedEnergyNature.LIGHTNING) {
            return cost * 10.0F;
        }
        return cost;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CursedEnergyFlowForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            // Damage
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker.level().isClientSide) return;

            boolean melee = !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SOUL));

            // If not enabled, then enable
            if (attacker instanceof ISorcerer && !JJKAbilities.hasToggled(attacker, JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                AbilityHandler.trigger(attacker, JJKAbilities.CURSED_ENERGY_FLOW.get());
            }

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

                            if (!attacker.level().isClientSide) {
                                for (int i = 0; i < 8; i++) {
                                    double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                                    double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                                    double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                                    ((ServerLevel) attacker.level()).sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColor(attacker), 0.5F, 1),
                                            victim.getX() + offsetX, victim.getY() + offsetY, victim.getZ() + offsetZ,
                                            0, 0.0D, 0.0D, 0.0D, 0.0D);
                                }
                            }
                        }
                    }
                }
            }

            // Shield
            if (victim instanceof Mob && !JJKAbilities.hasToggled(victim, JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                AbilityHandler.trigger(victim, JJKAbilities.CURSED_ENERGY_FLOW.get());
            }
            if (!JJKAbilities.hasToggled(victim, JJKAbilities.CURSED_ENERGY_FLOW.get())) return;

            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                if (victimCap.getNature() == CursedEnergyNature.LIGHTNING) {
                    if ((source.getDirectEntity() instanceof JujutsuLightningEntity) || (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu &&
                            jujutsu.getAbility() != null && jujutsu.getAbility().getClassification() == Classification.LIGHTNING)) {
                        event.setCanceled(true);
                    }
                }

                event.setAmount(event.getAmount() * 0.75F);

                switch (victimCap.getNature()) {
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