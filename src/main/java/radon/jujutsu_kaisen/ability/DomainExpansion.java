package radon.jujutsu_kaisen.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;

import java.util.List;

public abstract class DomainExpansion extends Ability {
    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    protected abstract int getRadius();
    protected abstract int getDuration();
    protected abstract Block getBlock();

    protected abstract void onHit(LivingEntity owner, Entity entity);

    private void createBarrier(LivingEntity owner) {
        int radius = this.getRadius();
        int duration = this.getDuration();
        Block block = this.getBlock();

        BlockPos center = owner.blockPosition().offset(0, radius - 1 - 1, 0);

        AABB bounds = new AABB(owner.getX() - radius, owner.getY(), owner.getZ() - radius,
                owner.getX() + radius, owner.getY() + radius, owner.getZ() + radius);
        List<Entity> entities = owner.level.getEntities(owner, bounds);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius) {
                        for (Entity entity : entities) {
                            if (entity.blockPosition().equals(pos)) {
                                this.onHit(owner, entity);
                            }
                        }

                        int delay = radius - y;

                        if (distance >= radius - 1) {
                            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                                cap.delayTickEvent(ownerClone -> {
                                    ownerClone.level.setBlockAndUpdate(pos, block.defaultBlockState());
                                }, delay);
                            });
                        } else {
                            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                                cap.delayTickEvent(ownerClone -> {
                                    ownerClone.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 16 | 32);
                                }, delay);
                            });
                        }
                    }
                }
            }
        }

        owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 4));
        owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 4));
        owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 4));
        owner.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, 4));
    }

    @Override
    public void run(LivingEntity owner) {
        this.createBarrier(owner);
    }
}
