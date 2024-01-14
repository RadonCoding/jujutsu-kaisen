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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Cleave extends Ability implements Ability.IDomainAttack, Ability.IAttack, Ability.IToggled {
    public static final double RANGE = 30.0D;
    private static final float MAX_DAMAGE = 25.0F;

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
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public void performEntity(LivingEntity owner, @Nullable DomainExpansionEntity domain, @Nullable LivingEntity target) {
        if (target == null) return;

        if (!(owner.level() instanceof ServerLevel level)) return;

        owner.level().playSound(null, target.getX(), target.getY(), target.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER,
                1.0F, 1.0F);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (int i = 1; i <= 20; i++) {
            cap.delayTickEvent(() -> {
                if (!target.isDeadOrDying()) {
                    level.sendParticles(JJKParticles.SLASH.get(), target.getX(), target.getY(), target.getZ(), 0, target.getId(),
                            0.0D, 0.0D, 1.0D);

                    Vec3 center = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
                    ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                }
            }, i);
        }

        for (int i = 1; i <= 10; i++) {
            cap.delayTickEvent(() -> {
                if (!target.isDeadOrDying()) {
                    owner.level().playSound(null, target.getX(), target.getY(), target.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER,
                            1.0F, 1.0F);
                }
            }, i * 2);
        }

        cap.delayTickEvent(() -> {
            DamageSource source = this.getSource(owner, domain);
            float damage = this.calculateDamage(source, owner, target);

            if (domain != null) {
                damage *= ((ConfigHolder.SERVER.maximumDomainSize.get().floatValue() + 0.1F) - cap.getDomainSize());
            }

            boolean success = target.hurt(source, damage);

            if (!success || !(target instanceof Mob) && !(target instanceof Player)) return;

            owner.level().playSound(null, target.getX(), target.getY(), target.getZ(), JJKSounds.CLEAVE.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }, 20);
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (!HelperMethods.isMelee(source)) return false;
        this.performEntity(owner, null, target);
        return true;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}
