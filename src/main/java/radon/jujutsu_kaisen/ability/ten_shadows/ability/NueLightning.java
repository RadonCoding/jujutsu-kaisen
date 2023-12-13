package radon.jujutsu_kaisen.ability.ten_shadows.ability;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.TenShadowsMode;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.NueEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class NueLightning extends Ability implements Ability.IToggled {
    private static final float DAMAGE = 10.0F;
    private static final int STUN = 20;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.distanceTo(target) < 5.0D;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!super.isValid(owner)) return false;
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return !cap.hasToggled(JJKAbilities.NUE.get()) &&
                cap.hasTamed(owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.NUE.get()) &&
                cap.getMode() == TenShadowsMode.ABILITY;
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
    public Classification getClassification() {
        return Classification.LIGHTNING;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class NueLightningForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker.level().isClientSide) return;

            LivingEntity victim = event.getEntity();

            boolean melee = !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SOUL));

            if (!melee) return;

            if (!JJKAbilities.hasToggled(attacker, JJKAbilities.NUE_LIGHTNING.get())) return;

            victim.invulnerableTime = 0;

            if (victim.hurt(JJKDamageSources.jujutsuAttack(attacker, JJKAbilities.NUE_LIGHTNING.get()), DAMAGE * Ability.getPower(JJKAbilities.NUE_LIGHTNING.get(), attacker))) {
                victim.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), STUN, 0, false, false, false));

                attacker.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                        SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.MASTER, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);

                for (int i = 0; i < 32; i++) {
                    double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                    double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                    double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                    ((ServerLevel) attacker.level()).sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.PURPLE_LIGHTNING, 0.5F, 1),
                            victim.getX() + offsetX, victim.getY() + offsetY, victim.getZ() + offsetZ,
                            0, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}