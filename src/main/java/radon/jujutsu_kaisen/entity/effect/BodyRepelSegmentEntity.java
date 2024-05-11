package radon.jujutsu_kaisen.entity.effect;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.JJKPartEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BodyRepelSegmentEntity extends JJKPartEntity<BodyRepelEntity> implements GeoEntity {
    public static final ResourceLocation RENDERER = new ResourceLocation(JujutsuKaisen.MOD_ID, "body_repel_segment");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BodyRepelSegmentEntity(BodyRepelEntity parent) {
        super(parent);

        this.setSize(EntityDimensions.fixed(1.25F, 1.1875F));
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
        if (!(entity instanceof BodyRepelEntity)) {
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
}