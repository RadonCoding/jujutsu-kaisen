package radon.jujutsu_kaisen.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.sound.JJKSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class WheelEntity extends Entity implements GeoEntity {
    private static final double OFFSET = 0.1D;
    private static final int SPIN_TIME = 20;
    private static final float STEP = -45.0F;

    private static final EntityDataAccessor<Integer> DATA_SPIN = SynchedEntityData.defineId(WheelEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public WheelEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public WheelEntity(LivingEntity owner) {
        super(JJKEntities.WHEEL.get(), owner.level);

        this.setOwner(owner);

        this.moveTo(owner.getX(), owner.getY() + owner.getBbHeight() + OFFSET, owner.getZ(),
                owner.getYRot(), 0.0F);
    }

    public void spin() {
        this.entityData.set(DATA_SPIN, SPIN_TIME);
        this.playSound(JJKSounds.WHEEL.get(), 5.0F, 1.0F);
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
        LivingEntity owner = this.getOwner();

        if (!this.level.isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive() || !JJKAbilities.hasToggled(owner, JJKAbilities.WHEEL.get()))) {
            this.discard();
        } else {
            if (owner != null) {
                int spin = this.entityData.get(DATA_SPIN);

                float yRot = owner.getYRot();

                if (spin > 0) {
                    yRot += STEP / SPIN_TIME * (SPIN_TIME - spin);
                    this.entityData.set(DATA_SPIN, --spin);
                }

                Vec3 movement = owner.getDeltaMovement();
                this.moveTo(owner.getX() + movement.x(), owner.getY() + owner.getBbHeight() + OFFSET, owner.getZ() + movement.z(),
                        yRot, 0.0F);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SPIN, 0);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.entityData.set(DATA_SPIN, pCompound.getInt("spin"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putFloat("spin", this.entityData.get(DATA_SPIN));
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            if (JJKAbilities.hasToggled(owner, JJKAbilities.WHEEL.get())) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    cap.toggle(owner, JJKAbilities.WHEEL.get());
                });
            }
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        Entity entity = this.level.getEntity(pPacket.getData());

        if (entity != null) {
            this.setOwner((LivingEntity) entity);
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
