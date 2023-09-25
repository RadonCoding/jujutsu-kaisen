package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Spiderweb extends Ability {
    private static final int RANGE = 3;
    private static final int DELAY = 20;
    private static final float EXPLOSIVE_POWER = 10.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && HelperMethods.RANDOM.nextInt(10) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable BlockHitResult getBlockHit(LivingEntity owner) {
        Vec3 start = owner.getEyePosition();
        Vec3 look = HelperMethods.getLookAngle(owner);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = HelperMethods.getHitResult(owner, start, end);

        if (result.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) result;
        } else if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            Vec3 offset = entity.position().subtract(0.0D, 5.0D, 0.0D);
            return owner.level.clip(new ClipContext(entity.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level instanceof ServerLevel level)) return;

        owner.swing(InteractionHand.MAIN_HAND, true);

        BlockHitResult hit = this.getBlockHit(owner);

        if (hit != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                Vec3 center = hit.getBlockPos().getCenter();

                AABB bounds = AABB.ofSize(center, EXPLOSIVE_POWER, 1.0D, EXPLOSIVE_POWER);

                for (int i = 0; i < HelperMethods.RANDOM.nextInt(DELAY / 4, DELAY / 2); i++) {
                    cap.delayTickEvent(() -> {
                        owner.level.playSound(null, center.x(), center.y(), center.z(),
                                JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);

                        BlockPos.betweenClosedStream(bounds).forEach(pos -> {
                            if (HelperMethods.RANDOM.nextInt(10) == 0) {
                                level.sendParticles(ParticleTypes.SWEEP_ATTACK, pos.getX(), pos.getY(), pos.getZ(),
                                        0, 0.0D, 0.0D, 0.0D, 0.0D);
                            }
                        });
                    }, i * 2);
                }
                cap.delayTickEvent(() ->
                        owner.level.explode(owner, center.x(), center.y(), center.z(), EXPLOSIVE_POWER * cap.getGrade().getRealPower(owner),
                                owner.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ?
                                        Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE), DELAY);
            });
        }
    }


    @Override
    public Status checkTriggerable(LivingEntity owner) {
        BlockHitResult hit = this.getBlockHit(owner);

        if (hit == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }
}