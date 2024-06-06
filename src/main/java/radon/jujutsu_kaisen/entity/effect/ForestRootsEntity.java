package radon.jujutsu_kaisen.entity.effect;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.JujutsuProjectile;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class ForestRootsEntity extends JujutsuProjectile implements GeoEntity {
    private static final int DURATION = 5 * 20;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private UUID victimUUID;
    @Nullable
    private Entity cachedVictim;

    private Vec3 pos;

    public ForestRootsEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public ForestRootsEntity(LivingEntity owner, float power, Entity target) {
        this(JJKEntities.FOREST_ROOTS.get(), target.level());

        this.setOwner(owner);
        this.setPower(power);

        this.setVictim(target);

        this.pos = target.position();

        this.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), 0.0F);
    }

    @Override
    public void tick() {
        Entity victim = this.getVictim();

        if (!this.level().isClientSide && (victim == null || victim.isRemoved() || !victim.isAlive())) {
            this.discard();
        } else {
            super.tick();

            if (this.getTime() >= DURATION) {
                this.discard();
            } else if (victim != null) {
                if (this.pos != null) {
                    victim.teleportTo(this.pos.x, this.pos.y, this.pos.z);
                }
            }
        }
    }

    public void setVictim(@Nullable Entity victim) {
        if (victim != null) {
            this.victimUUID = victim.getUUID();
            this.cachedVictim = victim;
        }
    }

    @Nullable
    public Entity getVictim() {
        if (this.cachedVictim != null && !this.cachedVictim.isRemoved()) {
            return this.cachedVictim;
        } else if (this.victimUUID != null && this.level() instanceof ServerLevel) {
            this.cachedVictim = ((ServerLevel) this.level()).getEntity(this.victimUUID);
            return this.cachedVictim;
        } else {
            return null;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        pCompound.putDouble("pos_x", this.pos.x);
        pCompound.putDouble("pos_y", this.pos.y);
        pCompound.putDouble("pos_z", this.pos.z);

        if (this.victimUUID != null) {
            pCompound.putUUID("victim", this.victimUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        this.pos = new Vec3(pCompound.getDouble("pos_x"), pCompound.getDouble("pos_y"), pCompound.getDouble("pos_z"));

        if (pCompound.hasUUID("victim")) {
            this.victimUUID = pCompound.getUUID("victim");
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}