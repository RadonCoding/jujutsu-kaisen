package radon.jujutsu_kaisen.ability.ten_shadows.ability;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
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


public class NueLightning extends Ability implements Ability.ITenShadowsAttack {
    private static final double RANGE = 3.0D;
    private static final float DAMAGE = 1.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        if (!super.isUnlocked(owner)) return false;
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return !cap.hasToggled(JJKAbilities.NUE.get()) &&
                        cap.hasTamed(owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.NUE.get()) &&
                        cap.getMode() == TenShadowsMode.ABILITY;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            return hit.getEntity() instanceof LivingEntity target ? target : null;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target != null) {
            owner.swing(InteractionHand.MAIN_HAND);
            this.perform(owner, target);
        }
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) return Status.FAILURE;

        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Status.FAILURE;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner.level() instanceof ServerLevel level) {
            if (cap.hasSummonOfClass(level, NueEntity.class)) return Status.FAILURE;
        }
        return super.checkStatus(owner);
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public void perform(LivingEntity owner, @Nullable LivingEntity target) {
        if (target == null || owner.level().isClientSide) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (target.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * cap.getAbilityPower(owner))) {
            target.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 2 * 20, 0, false, false, false));

            owner.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.NEUTRAL, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);

            for (int i = 0; i < 32; i++) {
                double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                ((ServerLevel) owner.level()).sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.VIOLET_LIGHTNING_COLOR, 0.5F, 10),
                        target.getX() + offsetX, target.getY() + offsetY, target.getZ() + offsetZ,
                        0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.LIGHTNING;
    }
}
