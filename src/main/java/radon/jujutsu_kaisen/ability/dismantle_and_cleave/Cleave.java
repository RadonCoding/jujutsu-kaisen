package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Cleave extends Ability implements Ability.IDomainAttack, Ability.IAttack, Ability.IToggled {
    public static final double RANGE = 30.0D;
    private static final float MAX_DAMAGE = 35.0F;

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

    private static DamageSource getSource(LivingEntity owner, @Nullable DomainExpansionEntity domain) {
        return domain == null ? JJKDamageSources.jujutsuAttack(owner, JJKAbilities.CLEAVE.get()) : JJKDamageSources.indirectJujutsuAttack(domain, owner, JJKAbilities.CLEAVE.get());
    }

    private static float calculateDamage(DamageSource source, LivingEntity owner, LivingEntity target) {
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
        return damage;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    public static void perform(LivingEntity owner, LivingEntity target, @Nullable DomainExpansionEntity domain, DamageSource source) {
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
                    Vec3 offset = center.add((HelperMethods.RANDOM.nextDouble() - 0.5D) * target.getBbWidth(),
                            (HelperMethods.RANDOM.nextDouble() - 0.5D) * target.getBbHeight(),
                            (HelperMethods.RANDOM.nextDouble() - 0.5D) * target.getBbWidth());
                    ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION, offset.x, offset.y, offset.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
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
            float power = domain == null ? Ability.getPower(JJKAbilities.CLEAVE.get(), owner) : Ability.getPower(JJKAbilities.CLEAVE.get(), owner) * DomainExpansion.getStrength(owner, false);

            float damage = calculateDamage(source, owner, target);
            damage = Math.min(MAX_DAMAGE * power, damage);

            boolean success = target.hurt(source, damage);

            if (!success || !(target instanceof Mob) && !(target instanceof Player)) return;

            owner.level().playSound(null, target.getX(), target.getY(), target.getZ(), JJKSounds.CLEAVE.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }, 20);
    }

    public static void perform(LivingEntity owner, LivingEntity target, @Nullable DomainExpansionEntity domain) {
        DamageSource source = getSource(owner, domain);
        perform(owner, target, domain, source);
    }

    @Override
    public void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain) {
        perform(owner, target, domain);
    }

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return false;
        if (!HelperMethods.isMelee(source)) return false;

        perform(owner, target, null);

        return true;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}
