package radon.jujutsu_kaisen.ability.projection_sorcery;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.particle.MirageParticle;
import radon.jujutsu_kaisen.client.particle.ProjectionParticle;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.effect.ProjectionFrameEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ScreenFlashS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncProjectionSorceryDataS2CPacket;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.ParticleUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectionSorcery extends Ability implements IChanneled, IDurationable {
    private static final double LAUNCH_POWER = 2.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.isChanneling(this)) {
            return HelperMethods.RANDOM.nextInt(5) != 0;
        }
        return HelperMethods.RANDOM.nextInt(20) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        if (owner.hasEffect(JJKEffects.STUN.get())) {
            return Status.FAILURE;
        }
        return super.isStillUsable(owner);
    }

    @Override
    public int getRealDuration(LivingEntity owner) {
        return 12;
    }

    private static float getYaw(Vec3 from, Vec3 to) {
        Vec3 delta = to.subtract(from);
        double dx = delta.x;
        double dz = delta.z;
        return -(float) Math.toDegrees(Math.atan2(dx, dz));
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IProjectionSorceryData data = cap.getProjectionSorceryData();

        List<AbstractMap.SimpleEntry<Vec3, Float>> frames = data.getFrames();

        if (frames.size() == 24) return;

        int charge = this.getCharge(owner) + 1;

        Vec3 start = owner.getEyePosition();
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 end = start.add(look.scale(charge * 4));
        HitResult result = RotationUtil.getHitResult(owner, start, end);

        Vec3 next;

        if (result.getType() == HitResult.Type.MISS) {
            next = end;
        } else {
            BlockPos pos = BlockPos.containing(result.getLocation().subtract(look));
            next = pos.getCenter().subtract(0.0D, 0.5D, 0.0D);
        }

        int index = this.getCharge(owner);

        float nextYaw;

        if (index > 0 && index <= frames.size()) {
            AbstractMap.SimpleEntry<Vec3, Float> entry = frames.get(index - 1);

            Vec3 current = entry.getKey();

            nextYaw = getYaw(current, next);

            if (frames.size() + 1 < 24) {
                Vec3 middle = current.add(next.subtract(current).scale(0.5D));
                float middleYaw = getYaw(middle, next);
                data.addFrame(middle, middleYaw);

                if (owner instanceof ServerPlayer player) {
                    ParticleUtil.sendParticle(player, new ProjectionParticle.ProjectionParticleOptions(owner.getId(), middleYaw), false, middle.x, middle.y, middle.z,
                            0.0D, 0.0D, 0.0D);
                }
            }
        } else {
            nextYaw = owner.getYRot();
        }
        data.addFrame(next, nextYaw);

        if (owner instanceof ServerPlayer player) {
            ParticleUtil.sendParticle(player, new ProjectionParticle.ProjectionParticleOptions(owner.getId(), nextYaw), false, next.x, next.y, next.z,
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    private static boolean isGrounded(Level level, BlockPos pos) {
        BlockHitResult hit = level.clip(new ClipContext(pos.getCenter(), pos.below(24).getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
        return hit.getType() == HitResult.Type.BLOCK;
    }

    @Override
    public void onStop(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData abilityData = cap.getAbilityData();
        IProjectionSorceryData projectionSorceryData = cap.getProjectionSorceryData();

        List<AbstractMap.SimpleEntry<Vec3, Float>> frames = new ArrayList<>(projectionSorceryData.getFrames());

        projectionSorceryData.resetFrames();

        if (frames.size() < 24) {
            return;
        }

        int delay = 0;

        AtomicBoolean cancelled = new AtomicBoolean();
        AtomicReference<Vec3> previous = new AtomicReference<>();

        for (AbstractMap.SimpleEntry<Vec3, Float> entry : frames) {
            Vec3 frame = entry.getKey();
            float yaw = entry.getValue();

            abilityData.delayTickEvent(() -> {
                if (cancelled.get()) return;

                owner.walkAnimation.setSpeed(2.0F);

                boolean isOnGround = isGrounded(owner.level(), owner.blockPosition()) || (previous.get() != null && isGrounded(owner.level(), BlockPos.containing(previous.get())));

                if ((!isOnGround && !owner.level().getBlockState(BlockPos.containing(frame)).canOcclude()) || frame.distanceTo(owner.position()) >= 24.0D * (projectionSorceryData.getSpeedStacks() + 1)) {
                    cancelled.set(true);

                    owner.level().addFreshEntity(new ProjectionFrameEntity(owner, owner, Ability.getOutput(JJKAbilities.TWENTY_FOUR_FRAME_RULE.get(), owner)));

                    if (owner instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new ScreenFlashS2CPacket(), player);
                    }
                    return;
                }
                if (owner.level() instanceof ServerLevel level) {
                    level.sendParticles(new MirageParticle.MirageParticleOptions(owner.getId()), owner.getX(), owner.getY(), owner.getZ(),
                            0, 0.0D, 0.0D, 0.0D, 1.0D);
                }
                AABB bounds = owner.getBoundingBox().inflate(2.0D);

                for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, owner.level(), owner, AABB.ofSize(frame, bounds.getXsize(), bounds.getYsize(), bounds.getZsize()))) {
                    if (!owner.hasLineOfSight(entity)) continue;

                    owner.swing(InteractionHand.MAIN_HAND, true);

                    if (owner instanceof Player player) {
                        player.attack(entity);
                    } else {
                        owner.doHurtTarget(entity);
                    }
                }

                Set<RelativeMovement> movements = EnumSet.noneOf(RelativeMovement.class);
                movements.add(RelativeMovement.X);
                movements.add(RelativeMovement.Y);
                movements.add(RelativeMovement.Z);
                movements.add(RelativeMovement.X_ROT);
                movements.add(RelativeMovement.Y_ROT);

                owner.teleportTo((ServerLevel) owner.level(), frame.x, frame.y, frame.z, movements, yaw, owner.getXRot());
                owner.setOnGround(true);

                previous.set(frame);
            }, delay++);
        }

        projectionSorceryData.addSpeedStack();

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(projectionSorceryData.serializeNBT()), player);
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IProjectionSorceryData data = cap.getProjectionSorceryData();

            if (data.getSpeedStacks() == 0) return;

            float speed = attacker.walkDist - attacker.walkDistO;

            if (speed <= 0.0F) return;

            Vec3 pos = victim.position().add(0.0D, victim.getBbHeight() / 2.0F, 0.0D);
            ((ServerLevel) victim.level()).sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
            victim.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(attacker);

            victim.setDeltaMovement(look.scale(LAUNCH_POWER * speed));
            victim.hurtMarked = true;

            event.setAmount(event.getAmount() * speed);
        }
    }
}
