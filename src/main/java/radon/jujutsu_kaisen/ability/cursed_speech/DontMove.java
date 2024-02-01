package radon.jujutsu_kaisen.ability.cursed_speech;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.IImbuement;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.List;

public class DontMove extends Ability implements Ability.IImbued {
    private static final double RANGE = 20.0D;
    private static final double RADIUS = 1.0D;
    private static final int DURATION = 20;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return getEntities(owner).contains(target) && HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public Ability.ActivationType getActivationType(LivingEntity owner) {
        return Ability.ActivationType.INSTANT;
    }

    private static List<LivingEntity> getEntities(LivingEntity owner) {
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 src = owner.getEyePosition();
        AABB bounds = AABB.ofSize(src, 1.0D, 1.0D, 1.0D).expandTowards(look.scale(RANGE)).inflate(RADIUS);
        return owner.level().getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity != owner);
    }

    @Override
    public void run(LivingEntity owner, Entity target) {
        if (!(target instanceof LivingEntity living)) return;

        if (JJKAbilities.hasToggled(living, JJKAbilities.INFINITY.get())) return;

        living.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), Math.round(DURATION * this.getPower(owner)), 1, false, false, false));

        if (living instanceof Player player) {
            player.sendSystemMessage(Component.translatable(String.format("chat.%s.dont_move", JujutsuKaisen.MOD_ID), owner.getName()));
        }
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

        for (LivingEntity entity : getEntities(owner)) {
            this.run(owner, entity);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.CURSED_SPEECH;
    }
}
