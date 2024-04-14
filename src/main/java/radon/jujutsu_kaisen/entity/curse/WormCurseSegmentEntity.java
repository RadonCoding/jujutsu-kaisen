package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class WormCurseSegmentEntity extends JJKPartEntity<WormCurseEntity> implements GeoEntity, Attackable {
    public static final ResourceLocation RENDERER = new ResourceLocation(JujutsuKaisen.MOD_ID, "worm_curse_segment");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WormCurseSegmentEntity(WormCurseEntity parent) {
        super(parent);

        this.setSize(EntityDimensions.fixed(0.9375F, 1.0625F));
    }

    @Override
    public void tick() {
        super.tick();

        this.collideWithOthers();
    }

    private void collideWithOthers() {
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox());

        for (Entity entity : entities) {
            if (entity.isPushable()) {
                this.collideWithEntity(entity);
            }
        }
    }

    private void collideWithEntity(Entity entity) {
        if (!(entity instanceof WormCurseEntity)) {
            entity.push(this);
        }
    }

    @Override
    public ResourceLocation getRenderer() {
        return RENDERER;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public float getStepHeight() {
        return 2.0F;
    }

    @Nullable
    @Override
    public LivingEntity getLastAttacker() {
        return this.getParent().getLastAttacker();
    }
}
