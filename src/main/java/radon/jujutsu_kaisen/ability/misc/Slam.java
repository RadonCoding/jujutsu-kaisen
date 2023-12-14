package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.*;

public class Slam extends Ability implements Ability.IChannelened {
    private static final double RANGE = 30.0D;
    private static final double LAUNCH_POWER = 2.0D;
    private static final float MAX_EXPLOSION = 5.0F;

    public static Map<UUID, Float> TARGETS = new HashMap<>();

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;
        if (!owner.hasLineOfSight(target)) return false;

        Vec3 look = owner.getLookAngle();
        Vec3 start = owner.getEyePosition();
        Vec3 result = target.getEyePosition().subtract(start);
        double angle = Math.acos(look.normalize().dot(result.normalize()));
        return angle < 1.0D;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    private Vec3 getTarget(LivingEntity owner) {
        Vec3 start = owner.getEyePosition();
        Vec3 look = owner.getLookAngle();
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = HelperMethods.getHitResult(owner, start, end);
        return result.getType() == HitResult.Type.MISS ? end : result.getLocation();
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner instanceof Player) || !owner.level().isClientSide) return;
        ClientWrapper.setOverlayMessage(Component.translatable(String.format("chat.%s.charge", JujutsuKaisen.MOD_ID),
                Math.round(((float) Math.min(20, this.getCharge(owner)) / 20) * 100)), false);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isMelee() {
        return true;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    public static void onHitGround(LivingEntity owner, float distance) {
        if (!(owner.level() instanceof ServerLevel)) return;

        float radius = Math.min(MAX_EXPLOSION, distance * TARGETS.get(owner.getUUID()));

        owner.swing(InteractionHand.MAIN_HAND, true);
        ExplosionHandler.spawn(owner.level().dimension(), owner.position(), radius, 5, Ability.getPower(JJKAbilities.SLAM.get(), owner) * 0.5F, owner,
                owner instanceof Player player ? owner.damageSources().playerAttack(player) : owner.damageSources().mobAttack(owner), false, false);
        TARGETS.remove(owner.getUUID());

        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SLAM.get(), SoundSource.MASTER, 1.0F, 1.0F);
    }

    @Override
    public void onRelease(LivingEntity owner) {
        if (!owner.onGround()) return;

        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        Vec3 target = this.getTarget(owner);

        Vec3 direction = new Vec3(0.0D, LAUNCH_POWER, 0.0D);
        owner.setDeltaMovement(owner.getDeltaMovement().add(direction));
        owner.hurtMarked = true;

        TARGETS.put(owner.getUUID(), ((float) Math.min(20, this.getCharge(owner)) / 20));

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        cap.delayTickEvent(() -> {
            owner.setDeltaMovement(owner.getDeltaMovement().add(target.subtract(owner.position()).normalize().scale(5.0D)));
            owner.hurtMarked = true;
        }, 20);
    }
}
