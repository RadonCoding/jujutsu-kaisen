package radon.jujutsu_kaisen.entity.domain;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.domain.base.OpenDomainExpansionEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChimeraShadowGardenEntity extends OpenDomainExpansionEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ChimeraShadowGardenEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ChimeraShadowGardenEntity(LivingEntity owner, DomainExpansion ability, int width, int height) {
        super(JJKEntities.CHIMERA_SHADOW_GARDEN.get(), owner, ability, width, height);
    }

    @Override
    public boolean hasSureHitEffect() {
        return false;
    }

    @Override
    public AABB getBounds() {
        int width = this.getWidth();
        int height = this.getHeight();
        return new AABB(this.getX() - width, this.getY(), this.getZ() - width,
                this.getX() + width, this.getY() - height, this.getZ() + width);
    }

    @Override
    public boolean isInsideBarrier(BlockPos pos) {
        if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity be && be.getIdentifier() != null && be.getIdentifier().equals(this.uuid))
            return true;

        int width = this.getWidth();
        int height = this.getHeight();
        BlockPos center = this.blockPosition().below();
        BlockPos relative = pos.subtract(center);
        return relative.getY() > -height && relative.distSqr(Vec3i.ZERO) < width * width;
    }

    private void createBarrier(Entity owner) {
        BlockPos center = this.blockPosition().below();

        int width = this.getWidth();
        int height = this.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int horizontal = i;
                int vertical = j;

                IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                IAbilityData data = cap.getAbilityData();

                int death = width - i;

                data.delayTickEvent(() -> {
                    for (int x = -horizontal; x <= horizontal; x++) {
                        for (int z = -horizontal; z <= horizontal; z++) {
                            double distance = Math.sqrt(x * x + vertical * vertical + z * z);

                            if (distance > horizontal || distance < horizontal - 1) continue;

                            BlockPos pos = center.offset(x, -vertical, z);

                            if (!this.level().isInWorldBounds(pos)) continue;

                            BlockState state = this.level().getBlockState(pos);

                            if (state.is(Blocks.BEDROCK)) continue;

                            if (this.isRemoved()) return;

                            BlockEntity existing = this.level().getBlockEntity(pos);

                            CompoundTag saved = null;

                            if (existing instanceof VeilBlockEntity be) {
                                be.destroy();

                                state = this.level().getBlockState(pos);
                            } else if (existing instanceof VeilBlockEntity be) {
                                be.destroy();

                                state = this.level().getBlockState(pos);
                            } else if (existing instanceof DomainBlockEntity be) {
                                BlockState original = be.getOriginal();

                                if (original == null) return;

                                state = original;
                                saved = be.getSaved();
                            } else if (existing != null) {
                                saved = existing.saveWithFullMetadata();
                            }

                            Block block = JJKBlocks.CHIMERA_SHADOW_GARDEN.get();

                            owner.level().removeBlockEntity(pos);

                            owner.level().setBlock(pos, block.defaultBlockState(),
                                    Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);

                            if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity be) {
                                be.create(this.uuid, death, state, saved);
                            }
                        }
                    }
                }, i);
            }
        }
    }

    @Override
    public boolean canBeHitByProjectile() {
        return this.isAlive();
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            if (!this.level().isClientSide) {
                if (this.getTime() - 1 == 0) {
                    this.createBarrier(owner);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
