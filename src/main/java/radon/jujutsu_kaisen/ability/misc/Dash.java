package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.MirageParticle;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Dash extends Ability {
    private static final List<UUID> JUMPED = new ArrayList<>();

    public static final double RANGE = 32.0D;
    private static final float DASH = 1.5F;
    private static final float MAX_DASH = 4.0F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isPhysical() {
        return true;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;
        return HelperMethods.RANDOM.nextInt(10) == 0 && owner.distanceTo(target) <= getRange(owner);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (!canDash(owner) && !canAirJump(owner)) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    private static boolean canAirJump(LivingEntity owner) {
        if (owner.hasEffect(JJKEffects.STUN.get())) return false;
        
        return JJKAbilities.AIR_JUMP.get().isUnlocked(owner) && (!JUMPED.contains(owner.getUUID()) || owner.getXRot() >= 15.0F);
    }

    private static boolean canDash(LivingEntity owner) {
        if (owner.hasEffect(JJKEffects.STUN.get())) return false;

        boolean collision = false;

        AABB bounds = owner.getBoundingBox();
        Cursor3D cursor = new Cursor3D(Mth.floor(bounds.minX - 1.0E-7D) - 1,
                Mth.floor(bounds.minY - 1.0E-7D) - 1,
                Mth.floor(bounds.minZ - 1.0E-7D) - 1,
                Mth.floor(bounds.maxX + 1.0E-7D) + 1,
                Mth.floor(bounds.maxY + 1.0E-7D) + 1,
                Mth.floor(bounds.maxZ + 1.0E-7D) + 1);

        while (cursor.advance()) {
            int i = cursor.nextX();
            int j = cursor.nextY();
            int k = cursor.nextZ();
            int l = cursor.getNextType();

            if (l == 3) continue;

            BlockState state = owner.level().getBlockState(new BlockPos(i, j, k));

            if (!state.isAir()) {
                collision = true;
                break;
            }
        }
        return collision;
    }

    private static float getRange(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData data = cap.getSorcererData();

        return (float) (RANGE * (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY) ? 2.0F : 1.0F));
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IProjectionSorceryData projectionSorceryData = cap.getProjectionSorceryData();

        if (projectionSorceryData.getSpeedStacks() > 0 || sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.DASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
            owner.addEffect(new MobEffectInstance(JJKEffects.INVISIBILITY.get(), 5, 0, false, false, false));
            level.sendParticles(new MirageParticle.MirageParticleOptions(owner.getId()), owner.getX(), owner.getY(), owner.getZ(),
                    0, 0.0D, 0.0D, 0.0D, 1.0D);
        }

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        HitResult hit = RotationUtil.getLookAtHit(owner, getRange(owner));

        float power = Math.min(MAX_DASH * (sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY) ? 1.5F : 1.0F),
                DASH * (1.0F + this.getOutput(owner) * 0.1F) * (sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY) ? 1.5F : 1.0F));

        if (hit.getType() == HitResult.Type.MISS) {
            float f = owner.getYRot();
            float f1 = owner.getXRot();
            float f2 = -Mth.sin(f * (Mth.PI / 180.0F)) * Mth.cos(f1 * (Mth.PI / 180.0F));
            float f3 = -Mth.sin(f1 * (Mth.PI / 180.0F));
            float f4 = Mth.cos(f * (Mth.PI / 180.0F)) * Mth.cos(f1 * (Mth.PI / 180.0F));
            float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
            f2 *= power / f5;
            f3 *= power / f5;
            f4 *= power / f5;
            owner.push(f2, f3, f4);
            owner.move(MoverType.SELF, new Vec3(0.0D, 1.1999999F, 0.0D));
        } else {
            Vec3 target = hit.getLocation();

            double distanceX = target.x - owner.getX();
            double distanceY = target.y - owner.getY();
            double distanceZ = target.z - owner.getZ();

            double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
            double motionX = distanceX / distance * power;
            double motionY = distanceY / distance * power;
            double motionZ = distanceZ / distance * power;

            owner.setDeltaMovement(motionX, motionY, motionZ);
        }
        owner.hurtMarked = true;

        Vec3 pos = owner.position();

        for (int i = 0; i < 32; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * 2 * Math.PI;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
            double r = HelperMethods.RANDOM.nextDouble() * 0.8D;
            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);
            Vec3 speed = look.add(x, y, z).reverse();
            level.sendParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 0, speed.x, speed.y, speed.z, 1.0D);
        }

        if (!canDash(owner) && canAirJump(owner)) {
            JUMPED.add(owner.getUUID());
        }
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return (!(owner instanceof ISorcerer sorcerer) || sorcerer.canJump()) && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 3 * 20;
    }

    @Override
    public int getRealCooldown(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData data = cap.getSorcererData();
        
        if (data == null) return 0;

        if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
            return 0;
        }
        return super.getRealCooldown(owner);
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.NONE;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity entity = event.getEntity();

            if (!JUMPED.contains(entity.getUUID())) return;
            if (!entity.onGround()) return;

            JUMPED.remove(entity.getUUID());
        }
    }
}
