package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class ShootRCT extends Ability {
    public static final float RANGE = 5.0F;

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
        if (target == null || !owner.hasLineOfSight(target)) return false;
        if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() == JujutsuType.CURSE && this.getTargets(owner).contains(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private List<LivingEntity> getTargets(LivingEntity owner) {
        Vec3 offset = owner.getEyePosition().add(owner.getLookAngle().scale(RANGE / 2));
        List<LivingEntity> entities = owner.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(offset, RANGE, RANGE, RANGE));
        entities.removeIf(entity -> entity == owner || entity instanceof DomainExpansionEntity);
        return entities;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        owner.swing(InteractionHand.MAIN_HAND, true);

        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (LivingEntity target : this.getTargets(owner)) {
            for (int i = 0; i < 8; i++) {
                ownerCap.delayTickEvent(() -> {
                    for (int j = 0; j < 8; j++) {
                        double x = target.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (target.getBbWidth() * 1.25F) - target.getLookAngle().scale(0.35D).x();
                        double y = target.getY() + HelperMethods.RANDOM.nextDouble() * (target.getBbHeight());
                        double z = target.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (target.getBbWidth() * 1.25F) - target.getLookAngle().scale(0.35D).z();
                        double speed = (target.getBbHeight() * 0.1F) * HelperMethods.RANDOM.nextDouble();
                        level.sendParticles(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.RCT_COLOR, target.getBbWidth() * 0.5F,
                                0.2F, 16), x, y, z, 0, 0.0D, speed, 0.0D, 1.0D);
                    }
                }, i * 2);
            }

            float amount = ConfigHolder.SERVER.sorcererHealingAmount.get().floatValue();

            if (target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                if (targetCap.getType() == JujutsuType.CURSE) {
                    target.hurt(JJKDamageSources.jujutsuAttack(owner, this), amount * ((float) (Math.max(1, HelperMethods.getGrade(ownerCap.getExperience()).ordinal())) / SorcererGrade.values().length) * 2.0F);
                    return;
                }
            }
            target.heal(amount * ((float) (Math.max(1, HelperMethods.getGrade(ownerCap.getExperience()).ordinal())) / SorcererGrade.values().length));
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return JJKAbilities.RCT.get().getCost(owner) * 2;
    }

    @Override
    public int getCooldown() {
        return 10;
    }

    @Override
    public List<Trait> getRequirements() {
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() != JujutsuType.CURSE && super.isValid(owner);
    }

}
