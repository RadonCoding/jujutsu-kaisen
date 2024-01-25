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
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
import radon.jujutsu_kaisen.util.RotationUtil;

public class NueLightning extends Ability implements Ability.IToggled, Ability.IAttack {
    private static final float DAMAGE = 5.0F;
    private static final int STUN = 20;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying() && owner.distanceTo(target) < 5.0D;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!super.isValid(owner)) return false;
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

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return false;
        if (!HelperMethods.isMelee(source)) return false;

        if (target.hurt(JJKDamageSources.jujutsuAttack(owner, JJKAbilities.NUE_LIGHTNING.get()), DAMAGE * Ability.getPower(JJKAbilities.NUE_LIGHTNING.get(), owner))) {
            target.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), STUN, 0, false, false, false));

            owner.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.MASTER, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);

            for (int i = 0; i < 32; i++) {
                double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                ((ServerLevel) owner.level()).sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.PURPLE_LIGHTNING, 0.5F, 1),
                        target.getX() + offsetX, target.getY() + offsetY, target.getZ() + offsetZ,
                        0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            return true;
        }
        return false;
    }
}
