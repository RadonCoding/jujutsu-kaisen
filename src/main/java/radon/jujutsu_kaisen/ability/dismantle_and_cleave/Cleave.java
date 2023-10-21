package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;



public class Cleave extends Ability implements Ability.IDomainAttack {
    public static final double RANGE = 30.0D;
    private static final float MAX_DAMAGE = 16.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            if (owner.canAttack(target)) {
                return target;
            }
        }
        return null;
    }

    private DamageSource getSource(LivingEntity owner, @Nullable DomainExpansionEntity domain) {
        return domain == null ? JJKDamageSources.jujutsuAttack(owner, this) : JJKDamageSources.indirectJujutsuAttack(domain, owner, this);
    }

    private float getMaxDamage(LivingEntity owner) {
        return MAX_DAMAGE * this.getPower(owner);
    }

    private float calculateDamage(DamageSource source, LivingEntity owner, LivingEntity target) {
        float damage = target.getMaxHealth();
        float armor = (float) target.getArmorValue();
        float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float f = 2.0F + toughness / 4.0F;
        float f1 = Mth.clamp(armor - damage / f, armor * 0.2F, 20.0F);
        damage /= 1.0F - f1 / 25.0F;

        MobEffectInstance instance = target.getEffect(MobEffects.DAMAGE_RESISTANCE);

        if (instance != null) {
            int resistance = instance.getAmplifier();
            int i = (resistance + 1) * 5;
            int j = 25 - i;

            if (j == 0) {
                return damage;
            } else {
                float x = 25.0F / (float) j;
                damage = damage * x;
            }
        }

        int k = EnchantmentHelper.getDamageProtection(target.getArmorSlots(), source);

        if (k > 0) {
            float f2 = Mth.clamp(k, 0.0F, 20.0F);
            damage /= 1.0F - f2 / 25.0F;
        }
        return Math.min(this.getMaxDamage(owner), damage);
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        this.perform(owner, null, target);
    }

    @Override
    public float getCost(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target != null && target.isAlive()) {
            return this.calculateDamage(this.getSource(owner, null), owner, target);
        }
        return 0.0F;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public void perform(LivingEntity owner, @Nullable DomainExpansionEntity domain, @Nullable LivingEntity target) {
        if (target != null && owner.level() instanceof ServerLevel level) {
            float padding = 0.5F;
            int count = Math.round(target.getBbHeight() / padding);

            owner.level().playSound(null, target.getX(), target.getY(), target.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);

            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            for (int i = 0; i < count; i++) {
                float offset = (i * padding);
                double width = target.getBbWidth();
                double x = target.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width * 2.0D;
                double y = target.getY() + offset;
                double z = target.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width * 2.0D;
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            cap.delayTickEvent(() -> {
                DamageSource source = this.getSource(owner, domain);
                float damage = this.calculateDamage(source, owner, target);

                if (domain != null) {
                    damage *= (1.6F - cap.getDomainSize());
                }

                boolean success = target.hurt(source, damage);

                for (int i = 0; i < count; i++) {
                    float offset = (i * padding);
                    double width = target.getBbWidth();
                    double x = target.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width * 2.0D;
                    double y = target.getY() + offset;
                    double z = target.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width * 2.0D;
                    level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                }
                owner.level().playSound(null, target.getX(), target.getY(), target.getZ(), JJKSounds.CLEAVE.get(), SoundSource.MASTER, 1.0F, 1.0F);

                if (!success || target instanceof SimpleDomainEntity || target instanceof DomainExpansionEntity) return;

                for (int i = 0; i < 12; i++) {
                    for (int j = 0; j < 16; j++) {
                        double d0 = HelperMethods.RANDOM.nextFloat() * 2.0F - 1.0F;
                        double d1 = HelperMethods.RANDOM.nextFloat() * 2.0F - 1.0F;
                        double d2 = HelperMethods.RANDOM.nextFloat() * 2.0F - 1.0F;

                        if (!(d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)) {
                            double d3 = target.getX(d0 / 4.0D);
                            double d4 = target.getY(0.5D + d1 / 4.0D);
                            double d5 = target.getZ(d2 / 4.0D);
                            ((ServerLevel) target.level()).sendParticles(JJKParticles.BLOOD.get(), d3, d4, d5,
                                    0, d0, d1 + 0.2D, d2, 1.0D);
                        }
                    }
                }
            }, count * 2);
        }
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }
}
