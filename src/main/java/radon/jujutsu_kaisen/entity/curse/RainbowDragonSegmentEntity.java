package radon.jujutsu_kaisen.entity.curse;


import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.JJKPartEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class RainbowDragonSegmentEntity extends JJKPartEntity<RainbowDragonEntity> implements GeoEntity, PlayerRideable, Attackable {
    public static final ResourceLocation RENDERER = new ResourceLocation(JujutsuKaisen.MOD_ID, "rainbow_dragon_segment");
    private static final EntityDataAccessor<Integer> DATA_INDEX = SynchedEntityData.defineId(RainbowDragonSegmentEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RainbowDragonSegmentEntity(RainbowDragonEntity parent, int index) {
        super(parent);

        this.setSize(EntityDimensions.fixed(1.0F, 1.0F));

        this.entityData.set(DATA_INDEX, index);
    }

    public int getIndex() {
        return this.entityData.get(DATA_INDEX);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_INDEX, 0);
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
        if (!(entity instanceof RainbowDragonEntity)) {
            entity.push(this);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Nullable
    @Override
    public LivingEntity getLastAttacker() {
        return this.getParent().getLastAttacker();
    }
}
