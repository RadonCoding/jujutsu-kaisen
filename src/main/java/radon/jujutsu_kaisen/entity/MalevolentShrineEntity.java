package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MalevolentShrineEntity extends OpenDomainExpansionEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MalevolentShrineEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public AABB getBounds() {
        int width = this.getWidth();
        int height = this.getHeight();
        return new AABB(this.getX() - width, this.getY(), this.getZ() - width,
                this.getX() + width, this.getY() + height, this.getZ() + width);
    }

    @Override
    public boolean isInsideBarrier(BlockPos pos) {
        int width = this.getWidth();
        int height = this.getHeight();
        BlockPos center = this.blockPosition().above(height / 2);
        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < width * width + height * height;
    }

    public MalevolentShrineEntity(LivingEntity owner, DomainExpansion ability, int width, int height, float strength) {
        super(JJKEntities.MALEVOLENT_SHRINE.get(), owner, ability, width, height, strength);
    }

    @Override
    protected void doSureHitEffect(@NotNull LivingEntity owner) {
        super.doSureHitEffect(owner);

        if (this.first) {
            BlockPos center = this.blockPosition();

            int width = this.getWidth();
            int height = this.getHeight();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int delay = i * 4;

                    int horizontal = i;
                    int vertical = j;

                    owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        cap.delayTickEvent(() -> {
                            if (this.isRemoved()) return;

                            for (int x = -horizontal; x <= horizontal; x++) {
                                for (int z = -horizontal; z <= horizontal; z++) {
                                    double distance = Math.sqrt(x * x + vertical * vertical + z * z);

                                    if (distance < horizontal && distance >= horizontal - 1) {
                                        BlockPos pos = center.offset(x, vertical, z);
                                        if (this.level.getBlockState(pos).isAir()) continue;
                                        this.ability.onHitBlock(this, owner, pos);
                                    }
                                }
                            }
                        }, delay);
                    });
                }
            }
            this.first = false;
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
