package radon.jujutsu_kaisen.ability.projection_sorcery;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.MirageParticle;
import radon.jujutsu_kaisen.client.particle.ProjectionParticle;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.effect.ProjectionFrameEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ScreenFlashS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectionSorcery extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final double LAUNCH_POWER = 3.0D;

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && (JJKAbilities.isChanneling(owner, this) || HelperMethods.RANDOM.nextInt(10) == 0);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        if (owner.hasEffect(JJKEffects.STUN.get())) {
            return Status.FAILURE;
        }
        return super.checkStatus(owner);
    }

    @Override
    public int getRealDuration(LivingEntity owner) {
        return 24;
    }

    @Override
    public void run(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        List<AbstractMap.SimpleEntry<Vec3, Float>> frames = cap.getFrames();

        if (frames.size() == 24) return;

        int charge = this.getCharge(owner) + 1;

        Vec3 start = owner.getEyePosition();
        Vec3 look = owner.getLookAngle();
        Vec3 end = start.add(look.scale(charge * 4));
        HitResult result = HelperMethods.getHitResult(owner, start, end);

        Vec3 pos = result.getType() == HitResult.Type.MISS ? end : result instanceof BlockHitResult block ? block.getBlockPos().getCenter().add(0.0D, 0.5D, 0.0D) : result.getLocation();

        int index = this.getCharge(owner) - 1;

        float yaw;

        if (index >= 0 && index < frames.size()) {
            AbstractMap.SimpleEntry<Vec3, Float> previous = frames.get(index);

            Vec3 delta = pos.subtract(previous.getKey());
            double dx = delta.x();
            double dz = delta.z();
            yaw = -(float) Math.toDegrees(Math.atan2(dx, dz));
        } else {
            yaw = owner.getYRot();
        }
        cap.addFrame(pos, yaw);

        owner.level().addParticle(new ProjectionParticle.ProjectionParticleOptions(owner.getId(), yaw), true, pos.x(), pos.y(), pos.z(),
                0.0D, 0.0D, 0.0D);
    }

    @Override
    public float getCost(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getFrames().size() == 24 ? 0.0F : 1.0F;
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
    public void onStart(LivingEntity owner) {

    }

    private static boolean isGrounded(Level level, BlockPos pos) {
        BlockHitResult hit = level.clip(new ClipContext(pos.getCenter(), pos.below(8).getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
        return hit.getType() == HitResult.Type.BLOCK;
    }

    @Override
    public void onRelease(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getFrames().size() < 24 / cap.getSpeedStacks()) {
            cap.resetFrames();
            return;
        }

        int delay = 0;

        AtomicBoolean cancelled = new AtomicBoolean();
        AtomicReference<Vec3> previous = new AtomicReference<>();

        for (AbstractMap.SimpleEntry<Vec3, Float> entry : cap.getFrames()) {
            Vec3 frame = entry.getKey();
            float yaw = entry.getValue();

            cap.delayTickEvent(() -> {
                if (cancelled.get()) return;

                owner.walkAnimation.setSpeed(2.0F);

                boolean isOnGround = isGrounded(owner.level(), owner.blockPosition()) || (previous.get() != null && isGrounded(owner.level(), BlockPos.containing(previous.get())));

                if ((!isOnGround && !owner.level().getBlockState(BlockPos.containing(frame)).canOcclude()) || frame.distanceTo(owner.position()) >= 24.0D) {
                    cancelled.set(true);

                    owner.level().addFreshEntity(new ProjectionFrameEntity(owner, owner, Ability.getPower(JJKAbilities.TWENTY_FOUR_FRAME_RULE.get(), owner)));

                    if (owner instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new ScreenFlashS2CPacket(), player);
                    }
                    cap.resetFrames();
                    return;
                }
                if (owner.level() instanceof ServerLevel level) {
                    level.sendParticles(new MirageParticle.MirageParticleOptions(owner.getId()), owner.getX(), owner.getY(), owner.getZ(),
                            0, 0.0D, 0.0D, 0.0D, 1.0D);
                }
                owner.moveTo(frame.x(), frame.y(), frame.z(), yaw, 0.0F);
                cap.removeFrame(entry);
                previous.set(frame);
            }, delay++);
        }
        cap.addSpeedStack();
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (!attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData cap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getSpeedStacks() == 0) return;

            float speed = attacker.walkDist - attacker.walkDistO;

            if (speed <= 0.0F) return;

            Vec3 pos = victim.position().add(0.0D, victim.getBbHeight() / 2.0F, 0.0D);
            ((ServerLevel) victim.level()).sendParticles(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 0, 1.0D, 0.0D, 0.0D, 1.0D);
            victim.level().playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

            Vec3 look = attacker.getLookAngle();

            victim.setDeltaMovement(look.scale(LAUNCH_POWER * speed));
            victim.hurtMarked = true;

            event.setAmount(event.getAmount() * speed);
        }
    }
}
