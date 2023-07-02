package radon.jujutsu_kaisen.ability.sukuna;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Cleave extends Ability implements Ability.IDomainAttack {
    public static final double RANGE = 30.0D;

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        LivingEntity result = null;

        if (owner instanceof Player) {
            EntityHitResult hit = HelperMethods.getEntityLookAt(owner, RANGE);

            if (hit != null && hit.getEntity() instanceof LivingEntity target) {
                result = target;
            }
        } else if (owner instanceof Mob mob) {
            LivingEntity target = mob.getTarget();

            if (target != null && mob.getSensing().hasLineOfSight(target)) {
                result = target;
            }
        }
        return result;
    }

    private static float getDamageAfterArmorAbsorb(LivingEntity target, float damage) {
        return CombatRules.getDamageAfterAbsorb(damage, (float) target.getArmorValue(), (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
    }

    private static float getArmorAbsorptionFactor(LivingEntity target, float damage) {
        return getDamageAfterArmorAbsorb(target, damage) / damage;
    }

    private static float calculateDamage(LivingEntity target) {
        float damage = target.getHealth() + target.getAbsorptionAmount();
        float armor = getArmorAbsorptionFactor(target, damage);
        return Math.min(50.0F, Math.max(1.0F, damage / armor));
    }

    @Override
    public void run(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target != null && target.isAlive()) {
            this.perform(owner, null, target);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target != null && target.isAlive()) {
            return calculateDamage(target);
        }
        return 0.0F;
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null || !target.isAlive()) {
            return Status.FAILURE;
        }
        return super.checkStatus(owner);
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public void perform(LivingEntity owner, @Nullable Entity indirect, @Nullable LivingEntity target) {
        if (target != null && owner.level instanceof ServerLevel level) {
            AABB bounds = target.getBoundingBox();
            double minY = bounds.minY;
            double maxY = bounds.maxY;

            double randomY = minY + (maxY - minY) * HelperMethods.RANDOM.nextDouble();
            level.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), randomY, target.getZ(),
                    0, 0.0D, 0.0D, 0.0D, 0.0D);

            DamageSource source = indirect == null ? DamageSource.mobAttack(owner) : DamageSource.indirectMobAttack(indirect, owner);

            float damage = calculateDamage(target);
            owner.level.playSound(null, target.getX(), target.getY(), target.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
            target.hurt(source, damage);
        }
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
