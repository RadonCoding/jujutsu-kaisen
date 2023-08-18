package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
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
        BlockPos center = this.blockPosition();

        int width = this.getWidth();
        int height = this.getHeight();

        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < width * height;
    }

    public MalevolentShrineEntity(LivingEntity owner, DomainExpansion ability, int width, int height, float strength) {
        super(JJKEntities.MALEVOLENT_SHRINE.get(), owner, ability, width, height, strength);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
