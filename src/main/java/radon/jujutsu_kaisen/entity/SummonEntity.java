package radon.jujutsu_kaisen.entity;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public abstract class SummonEntity extends TamableAnimal implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(SummonEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    protected SummonEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);

        this.setPathfindingMalus(PathType.LEAVES, 0.0F);
    }

    protected boolean requiresOwner() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_TIME, 0);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        LivingEntity passenger = this.getControllingPassenger();

        if (passenger != null) {
            this.setSprinting(new Vec3(passenger.xxa, passenger.yya, passenger.zza).lengthSqr() > 0.01D);
        } else {
            this.setSprinting(this.getDeltaMovement().lengthSqr() > 0.01D && this.moveControl.getSpeedModifier() > 1.0D);
        }
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        if (!super.canAttack(pTarget)) return false;

        if (!this.isTame()) return true;

        if (pTarget == this.getOwner()) return false;

        if (!(pTarget instanceof TamableAnimal)) return true;

        while (pTarget instanceof TamableAnimal tamable1) {
            if (!(tamable1.getOwner() instanceof TamableAnimal tamable2)) break;

            pTarget = tamable2;
        }
        return ((TamableAnimal) pTarget).getOwner() != this.getOwner() || ((TamableAnimal) pTarget).isTame() != this.isTame();
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
        super.actuallyHurt(pDamageSource, pDamageAmount);

        if (pDamageSource.getEntity() instanceof LivingEntity attacker && this.canAttack(attacker) && attacker != this) {
            this.setTarget(attacker);
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

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        this.updateSwingTime();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this instanceof ISorcerer sorcerer) {
            sorcerer.init();
        }

        if (this instanceof ICommandable commandable && commandable.canChangeTarget() && this.getOwner() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.set_target_info", JujutsuKaisen.MOD_ID)),
                    false));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("tame", this.isTame());

        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putInt("time", this.getTime());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setTame(pCompound.getBoolean("tame"), false);

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.setTime(pCompound.getInt("time"));
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && this.requiresOwner() && this.isTame() && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel pLevel, @NotNull AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public abstract Summon<?> getAbility();

    protected boolean shouldToggleOnDeath() {
        return true;
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);

        if (!this.shouldToggleOnDeath()) return;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        Ability ability = this.getAbility();

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(ability)) {
            data.toggle(ability);
        }
    }

    @Override
    public boolean isFood(@NotNull ItemStack pStack) {
        return false;
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
