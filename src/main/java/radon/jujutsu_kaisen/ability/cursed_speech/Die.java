package radon.jujutsu_kaisen.ability.cursed_speech;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.List;

public class Die extends Ability {
    private static final double RANGE = 20.0D;
    private static final double RADIUS = 1.0D;
    private static final float DAMAGE = 25.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return getEntities(owner).contains(target) && target != null && (owner.getHealth() / owner.getMaxHealth() <= 0.25F || HelperMethods.RANDOM.nextInt(10) == 0 && owner.hasLineOfSight(target));
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private static List<Entity> getEntities(LivingEntity owner) {
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 src = owner.getEyePosition();
        AABB bounds = AABB.ofSize(src, 1.0D, 1.0D, 1.0D).expandTowards(look.scale(RANGE)).inflate(RADIUS);
        return owner.level().getEntities(owner, bounds, entity -> !(entity instanceof LivingEntity living) || owner.canAttack(living));
    }

    private static float calculateDamage(DamageSource source, LivingEntity target) {
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
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        Vec3 src = owner.getEyePosition();

        for (int i = 1; i < RANGE + 7; i++) {
            Vec3 dst = src.add(look.scale(i));
            ((ServerLevel) owner.level()).sendParticles(JJKParticles.CURSED_SPEECH.get(), dst.x, dst.y, dst.z, 0, src.distanceTo(dst) * 0.5D, 0.0D, 0.0D, 1.0D);
        }

        owner.level().playSound(null, src.x, src.y, src.z, JJKSounds.CURSED_SPEECH.get(), SoundSource.MASTER, 2.0F, 0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F);

        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (Entity entity : getEntities(owner)) {
            if (!(entity instanceof LivingEntity living) || JJKAbilities.hasToggled(living, JJKAbilities.INFINITY.get())) continue;

            living.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getPower(owner));

            living.getCapability(SorcererDataHandler.INSTANCE).ifPresent(targetCap -> {
                if (SorcererUtil.getGrade(ownerCap.getExperience()).ordinal() - SorcererUtil.getGrade(targetCap.getExperience()).ordinal() >= 2) {
                    DamageSource source = JJKDamageSources.jujutsuAttack(owner, this);
                    entity.hurt(source, calculateDamage(source, living));
                }
            });

            if (living instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.die", JujutsuKaisen.MOD_ID), owner.getName()));
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.CURSED_SPEECH;
    }
}
