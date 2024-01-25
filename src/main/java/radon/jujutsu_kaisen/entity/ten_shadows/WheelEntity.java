package radon.jujutsu_kaisen.entity.ten_shadows;

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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.sound.JJKSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class WheelEntity extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_SPIN = SynchedEntityData.defineId(WheelEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation SPIN = RawAnimation.begin().thenPlay("misc.spin");

    private static final int SPIN_DURATION = 20;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public WheelEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public WheelEntity(LivingEntity owner) {
        this(JJKEntities.WHEEL.get(), owner.level());

        this.setOwner(owner);

        this.setPos(owner.position());

        this.startRiding(owner);
    }

    @Override
    public double getMyRidingOffset() {
        return 0.5D;
    }

    public void spin() {
        this.entityData.set(DATA_SPIN, SPIN_DURATION);
        this.playSound(JJKSounds.WHEEL.get(), 3.0F, 1.0F);
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
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive() ||
                !JJKAbilities.hasToggled(owner, JJKAbilities.WHEEL.get()))) {
            this.discard();
        } else {
            super.tick();

            if (!this.level().isClientSide) {
                int spin = this.entityData.get(DATA_SPIN);

                if (spin > 0) {
                    this.entityData.set(DATA_SPIN, --spin);
                }
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
        pCompound.putInt("spin", this.entityData.get(DATA_SPIN));
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            if (JJKAbilities.hasToggled(owner, JJKAbilities.WHEEL.get())) {
                ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                cap.toggle(JJKAbilities.WHEEL.get());
            }
        }
    }

    private PlayState spinPredicate(AnimationState<WheelEntity> animationState) {
        int spin = this.entityData.get(DATA_SPIN);

        if (spin > 0) {
            System.out.println(1337);
            return animationState.setAndContinue(SPIN);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Spin", this::spinPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        LivingEntity owner = (LivingEntity) this.level().getEntity(pPacket.getData());

        if (owner != null) {
            this.setOwner(owner);
        }
    }
}
