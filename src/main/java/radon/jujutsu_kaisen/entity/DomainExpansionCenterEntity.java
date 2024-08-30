package radon.jujutsu_kaisen.entity;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.DataProvider;
import radon.jujutsu_kaisen.data.domain.DomainData;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.domain.DomainExpansionEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class DomainExpansionCenterEntity extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(DomainExpansionCenterEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private UUID domainUUID;
    @Nullable
    private DomainExpansionEntity cachedDomain;

    public DomainExpansionCenterEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DomainExpansionCenterEntity(EntityType<?> pType, DomainExpansionEntity domain) {
        super(pType, domain.level());

        this.setDomain(domain);
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        DomainExpansionEntity domain = this.getDomain();

        Optional<IDomainData> data = DataProvider.getDataIfPresent(this.level(), JJKAttachmentTypes.DOMAIN);

        if (!this.level().isClientSide && (domain == null || domain.isRemoved() || !domain.isAlive()) &&
                (data.isEmpty() || !data.get().containsDomain(this.domainUUID))) {
            this.discard();
        } else {
            super.tick();
        }
    }

    @Nullable
    public DomainExpansionEntity getDomain() {
        if (this.cachedDomain != null && !this.cachedDomain.isRemoved()) {
            return this.cachedDomain;
        } else if (this.domainUUID != null && this.level() instanceof ServerLevel) {
            this.cachedDomain = (DomainExpansionEntity) ((ServerLevel) this.level()).getEntity(this.domainUUID);
            return this.cachedDomain;
        } else {
            return null;
        }
    }

    public void setDomain(@Nullable DomainExpansionEntity domain) {
        if (domain != null) {
            this.domainUUID = domain.getUUID();
            this.cachedDomain = domain;
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
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.domainUUID != null) {
            pCompound.putUUID("domain", this.domainUUID);
        }
        pCompound.putInt("time", this.getTime());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("domain")) {
            this.domainUUID = pCompound.getUUID("domain");
        }
        this.setTime(pCompound.getInt("time"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
