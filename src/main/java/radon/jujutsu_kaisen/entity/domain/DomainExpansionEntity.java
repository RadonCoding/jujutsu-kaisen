package radon.jujutsu_kaisen.entity.domain;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.event.LivingInsideDomainEvent;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.IDomain;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class DomainExpansionEntity extends Entity implements IDomain {
    public static final int OFFSET = 5;
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(DomainExpansionEntity.class, EntityDataSerializers.INT);
    protected DomainExpansion ability;
    protected boolean first = true;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;
    private float scale = 1.0F;
    protected boolean instant;

    protected DomainExpansionEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability) {
        super(pType, owner.level());

        this.setOwner(owner);

        this.ability = ability;
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public boolean isInstant() {
        return this.instant;
    }

    @Override
    public void setInstant(boolean instant) {
        this.instant = instant;
    }

    @Override
    public boolean ignoreExplosion(@NotNull Explosion pExplosion) {
        return true;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.level().isClientSide) return;

        VeilHandler.barrier(this.level().dimension(), this.getUUID());

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        for (LivingEntity entity : this.getAffected(this.level())) {
            NeoForge.EVENT_BUS.post(new LivingInsideDomainEvent(entity, this.ability, owner));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        pBuilder.define(DATA_TIME, 0);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putString("technique", JJKAbilities.getKey(this.ability).toString());
        pCompound.putBoolean("first", this.first);
        pCompound.putInt("time", this.getTime());
        pCompound.putFloat("scale", this.scale);
        pCompound.putBoolean("instant", this.instant);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.ability = (DomainExpansion) JJKAbilities.getValue(new ResourceLocation(pCompound.getString("technique")));
        this.first = pCompound.getBoolean("first");
        this.setTime(pCompound.getInt("time"));
        this.scale = pCompound.getFloat("scale");
        this.instant = pCompound.getBoolean("instant");
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    public List<LivingEntity> getAffected(Level level) {
        return level.getEntitiesOfClass(LivingEntity.class, this.instant ? this.getPhysicalBounds() : this.getVirtualBounds(), this::isAffected);
    }

    @Override
    public boolean hasSureHitEffect() {
        return true;
    }

    public DomainExpansion getAbility() {
        return this.ability;
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
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
    public boolean isInWall() {
        return false;
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && owner != null) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(this.ability)) {
                this.discard();
                return;
            }
        }

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();
        }
    }

    public boolean isAffected(LivingEntity victim) {
        LivingEntity owner = this.getOwner();

        if (owner == null || victim == owner) {
            return false;
        }

        if (!owner.canAttack(victim)) return false;

        IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap != null) {
            ITenShadowsData data = cap.getTenShadowsData();

            if ((victim instanceof MahoragaEntity && data.isAdaptedTo(this.ability))) return false;
        }

        for (SimpleDomainEntity simple : victim.level().getEntitiesOfClass(SimpleDomainEntity.class, AABB.ofSize(victim.position(),
                SimpleDomainEntity.MAX_RADIUS * 2, SimpleDomainEntity.MAX_RADIUS * 2, SimpleDomainEntity.MAX_RADIUS * 2))) {
            if (victim.distanceTo(simple) < simple.getRadius()) return false;
        }
        return this.isAffected(victim.blockPosition());
    }

    public boolean shouldCollapse(float strength) {
        return (strength / this.getStrength()) > 1.75F;
    }

    @Override
    public float getStrength() {
        return IDomain.super.getStrength();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 10.0D;

        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        d0 *= 64.0D * getViewScale();
        return pDistance < d0 * d0;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        LivingEntity entity = this.getOwner();
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
