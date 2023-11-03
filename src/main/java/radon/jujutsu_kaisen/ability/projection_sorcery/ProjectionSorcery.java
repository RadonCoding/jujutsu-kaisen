package radon.jujutsu_kaisen.ability.projection_sorcery;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.ProjectionParticle;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.effect.ProjectionFrameEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ScreenFlashS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectionSorcery extends Ability implements Ability.IChannelened {
    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && HelperMethods.RANDOM.nextInt(10) == 0;
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
    public void run(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getFrames().size() == 24) return;

        Vec3 start = owner.getEyePosition();
        Vec3 look = owner.getLookAngle();
        Vec3 end = start.add(look.scale(this.getCharge(owner) * 4));
        HitResult result = HelperMethods.getHitResult(owner, start, end);

        Vec3 pos = result.getType() == HitResult.Type.MISS ? end : result.getLocation().add(0.0D, owner.getBbHeight(), 0.0D);

        Vec3 frame = pos.subtract(0.0D, owner.getBbHeight(), 0.0D);
        cap.addFrame(frame);

        owner.level().addParticle(new ProjectionParticle.ProjectionParticleOptions(owner.getId()), frame.x(), frame.y(), frame.z(),
                0.0D, 0.0D, 0.0D);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
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

        if (cap.getFrames().size() < 24) {
            cap.resetFrames();
            return;
        }

        int delay = 0;

        AtomicBoolean cancelled = new AtomicBoolean();
        AtomicReference<Vec3> previous = new AtomicReference<>();

        for (Vec3 frame : cap.getFrames()) {
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
                owner.teleportTo(frame.x(), frame.y(), frame.z());
                cap.removeFrame(frame);

                previous.set(frame);
            }, delay++);
        }
    }
}
