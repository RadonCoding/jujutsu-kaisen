package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DurationBlockEntity;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;

public class WaterShield extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final double RADIUS = 3.0D;
    private static final double X_STEP = 0.05D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return !owner.level().getEntities(owner, owner.getBoundingBox().inflate(1.0D), entity -> entity instanceof Projectile).isEmpty();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (!owner.level().isClientSide) {
                for (double phi = 0.0D; phi < Math.PI * 2.0D; phi += X_STEP) {
                    for (int i = 0; i < RADIUS; i++) {
                        double x = owner.getX() + RADIUS * Math.cos(phi);
                        double y = owner.getY() + i;
                        double z = owner.getZ() + RADIUS * Math.sin(phi);

                        BlockPos pos = BlockPos.containing(x, y, z);
                        BlockState state = owner.level().getBlockState(pos);

                        if (!state.isAir() || state.canOcclude()) continue;

                        owner.level().setBlockAndUpdate(pos, JJKBlocks.FAKE_WATER_DURATION.get().defaultBlockState());

                        if (owner.level().getBlockEntity(pos) instanceof DurationBlockEntity be) {
                            be.create(1, state);
                        }
                    }
                }

                AABB bounds = AABB.ofSize(owner.position(), RADIUS, RADIUS, RADIUS).inflate(1.0D);

                for (Entity entity : owner.level().getEntities(owner, bounds)) {
                    entity.setDeltaMovement(entity.position().subtract(owner.position()).normalize());
                    entity.hurtMarked = true;
                }
            }
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public void onRelease(LivingEntity owner, int charge) {
        if (!owner.level().isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> owner.level().explode(owner,
                    JJKDamageSources.indirectJujutsuAttack(owner, owner, JJKAbilities.WATER_SHIELD.get()), null, owner.position(),
                cap.getGrade().getRealPower(owner) * 2.0F, false, Level.ExplosionInteraction.NONE));
        }
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }
}
