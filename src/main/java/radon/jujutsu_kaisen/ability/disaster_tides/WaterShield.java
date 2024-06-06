package radon.jujutsu_kaisen.ability.disaster_tides;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DurationBlockEntity;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class WaterShield extends Ability implements IChanneled, IDurationable {
    private static final double RADIUS = 3.0D;
    private static final double X_STEP = 0.05D;
    private static final float DAMAGE = 10.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.isChanneling(this)) {
            return HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return HelperMethods.RANDOM.nextInt(40) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        for (double phi = 0.0D; phi < Math.PI * 2; phi += X_STEP) {
            for (int i = 0; i < RADIUS; i++) {
                double x = owner.getX() + RADIUS * Math.cos(phi);
                double y = owner.getY() + i;
                double z = owner.getZ() + RADIUS * Math.sin(phi);

                BlockPos pos = BlockPos.containing(x, y, z);

                if (!owner.level().isInWorldBounds(pos)) continue;

                BlockState state = owner.level().getBlockState(pos);

                if (!state.isAir() || state.canOcclude()) continue;

                owner.level().setBlockAndUpdate(pos, JJKBlocks.FAKE_WATER_DURATION.get().defaultBlockState());

                if (owner.level().getBlockEntity(pos) instanceof DurationBlockEntity be) {
                    be.create(1, state);
                }
            }
        }

        AABB bounds = AABB.ofSize(owner.position(), RADIUS, RADIUS, RADIUS).inflate(1.0D);

        for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, owner.level(), owner, bounds)) {
            entity.setDeltaMovement(entity.position().subtract(owner.position()).normalize());
            entity.hurtMarked = true;
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public void onStop(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION, owner.getX(), owner.getY(), owner.getZ(), 0, 1.0D, 0.0D, 0.0D, 1.0D);
            ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, owner.getX(), owner.getY(), owner.getZ(), 0, 1.0D, 0.0D, 0.0D, 1.0D);
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                    4.0F, (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F);

            for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, owner.level(), owner, AABB.ofSize(owner.position(), RADIUS * 2, RADIUS * 2, RADIUS * 2))) {
                entity.hurt(JJKDamageSources.jujutsuAttack(owner, JJKAbilities.WATER_SHIELD.get()), DAMAGE * this.getOutput(owner));
            }
        }
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.WATER;
    }
}
