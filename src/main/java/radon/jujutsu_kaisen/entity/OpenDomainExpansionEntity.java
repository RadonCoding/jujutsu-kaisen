package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

public abstract class OpenDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_WIDTH = SynchedEntityData.defineId(OpenDomainExpansionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HEIGHT = SynchedEntityData.defineId(OpenDomainExpansionEntity.class, EntityDataSerializers.INT);

    public OpenDomainExpansionEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public OpenDomainExpansionEntity(EntityType<?> pEntityType, LivingEntity owner, DomainExpansion ability, int width, int height) {
        super(pEntityType, owner, ability);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle()
                        .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, owner.getXRot(), owner.getYRot());

        this.entityData.set(DATA_WIDTH, width);
        this.entityData.set(DATA_HEIGHT, height);
    }

    @Override
    public boolean isAffected(BlockPos pos) {
        if (VeilHandler.isProtected(this.level(), pos)) return false;

        for (DomainExpansionEntity domain : this.getDomains()) {
            if (domain.isInsideBarrier(pos)) return false;
        }
        return super.isAffected(pos);
    }

    public int getWidth() {
        return this.entityData.get(DATA_WIDTH);
    }

    public int getHeight() {
        return this.entityData.get(DATA_HEIGHT);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_WIDTH, 0);
        this.entityData.define(DATA_HEIGHT, 0);
    }

    @Override
    public void warn() {
        for (Entity entity : this.getAffected()) {
            entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.onInsideDomain(this));
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("width", this.getWidth());
        pCompound.putInt("height", this.getHeight());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_WIDTH, pCompound.getInt("width"));
        this.entityData.set(DATA_HEIGHT, pCompound.getInt("height"));
    }

    protected List<DomainExpansionEntity> getDomains() {
        List<DomainExpansionEntity> domains = HelperMethods.getEntityCollisionsOfClass(DomainExpansionEntity.class, this.level(), this.getBounds());
        domains.removeIf(domain -> domain.is(this));
        return domains;
    }

    protected void doSureHitEffect(@NotNull LivingEntity owner) {
        AABB bounds = this.getBounds();

        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, bounds, this::isAffected)) {
            this.ability.onHitEntity(this, owner, entity, false);
        }
    }

    @Override
    public boolean checkSureHitEffect() {
        for (DomainExpansionEntity domain : this.getDomains()) {
            if (domain instanceof ClosedDomainExpansionEntity closed && !closed.isInsideBarrier(this.blockPosition()))
                continue;

            if (this.shouldCollapse(domain.getStrength())) {
                this.discard();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.checkSureHitEffect()) {
            this.warn();
        }
    }

    @Override
    public void onRemovedFromWorld() {
        if (!this.level().isClientSide) {
            LivingEntity owner = this.getOwner();

            if (owner != null) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    cap.setBurnout(DomainExpansion.BURNOUT);

                    if (owner instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                    }
                });
            }
        }
        super.onRemovedFromWorld();
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            if (!this.level().isClientSide) {
                if (this.checkSureHitEffect()) {
                    this.warn();

                    this.doSureHitEffect(owner);
                }
            }
        }
    }
}
