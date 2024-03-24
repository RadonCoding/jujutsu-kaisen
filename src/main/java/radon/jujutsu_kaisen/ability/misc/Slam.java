package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.*;

public class Slam extends Ability implements ICharged {
    private static final double RANGE = 30.0D;
    private static final double LAUNCH_POWER = 2.0D;
    private static final float MAX_EXPLOSION = 5.0F;

    public static Map<UUID, Float> TARGETS = new HashMap<>();

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;

        if (owner.level().getGameRules().getRule(GameRules.RULE_MOBGRIEFING).get()) {
            BlockPos above = BlockPos.containing(owner.position()
                    .add(0.0D, owner.getBbHeight(), 0.0D)
                    .add(0.0D, 0.5D, 0.0D));

            if (owner.getNavigation().isStuck() && owner.level().getBlockState(above).getCollisionShape(owner.level(), above).isEmpty())
                return true;
        }
        return owner.hasLineOfSight(target);
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
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = RotationUtil.getHitResult(owner, start, end);
        return result.getType() == HitResult.Type.MISS ? end : result.getLocation();
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner instanceof Player) || !owner.level().isClientSide) return;
        ClientWrapper.setOverlayMessage(Component.translatable(String.format("chat.%s.charge", JujutsuKaisen.MOD_ID),
                Math.round(((float) Math.min(20, this.getCharge(owner)) / 20) * 100)), false);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return (!(owner instanceof ISorcerer sorcerer) || sorcerer.hasMeleeAttack() && sorcerer.canJump()) && super.isValid(owner);
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        if (owner.hasEffect(JJKEffects.STUN.get())) {
            return Status.FAILURE;
        }
        return super.isStillUsable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData data = cap.getSorcererData();

        if (data == null) return 0.0F;

        return data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY) ? 0.0F : 30.0F;
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
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    public static void onHitGround(LivingEntity owner, float distance) {
        if (owner.level().isClientSide) return;

        float radius = Math.min(MAX_EXPLOSION, distance * TARGETS.get(owner.getUUID()));

        owner.swing(InteractionHand.MAIN_HAND);

        if (!owner.level().isClientSide) {
            ExplosionHandler.spawn(owner.level().dimension(), owner.position(), radius, 5, Ability.getOutput(JJKAbilities.SLAM.get(), owner) * 0.25F,
                    owner, owner instanceof Player player ? owner.damageSources().playerAttack(player) : owner.damageSources().mobAttack(owner), false);
        }
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SLAM.get(), SoundSource.MASTER, 1.0F, 1.0F);

        TARGETS.remove(owner.getUUID());
    }

    @Override
    public boolean onRelease(LivingEntity owner) {
        int charge = this.getCharge(owner);

        if (charge == 0) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (!owner.onGround()) return false;

        owner.swing(InteractionHand.MAIN_HAND);

        Vec3 direction = new Vec3(0.0D, LAUNCH_POWER, 0.0D);
        owner.setDeltaMovement(owner.getDeltaMovement().add(direction));

        if (!owner.level().isClientSide) {
            TARGETS.put(owner.getUUID(), ((float) Math.min(20, charge) / 20));
        }

        data.delayTickEvent(() -> {
            Vec3 target = this.getTarget(owner);
            owner.setDeltaMovement(owner.getDeltaMovement().add(target.subtract(owner.position()).normalize().scale(5.0D)));
        }, 20);
        return true;
    }
}
