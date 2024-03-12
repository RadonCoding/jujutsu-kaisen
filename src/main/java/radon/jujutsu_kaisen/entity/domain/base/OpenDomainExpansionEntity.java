package radon.jujutsu_kaisen.entity.domain.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.Set;

public abstract class OpenDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_WIDTH = SynchedEntityData.defineId(OpenDomainExpansionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HEIGHT = SynchedEntityData.defineId(OpenDomainExpansionEntity.class, EntityDataSerializers.INT);

    public OpenDomainExpansionEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public OpenDomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability, int width, int height) {
        super(pType, owner, ability);

        Vec3 pos = owner.position()
                .subtract(RotationUtil.getTargetAdjustedLookAngle(owner)
                        .multiply(this.getBbWidth() / 2.0F, 0.0D, this.getBbWidth() / 2.0F));
        this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        this.entityData.set(DATA_WIDTH, width);
        this.entityData.set(DATA_HEIGHT, height);
    }

    @Override
    public void push(@NotNull Entity pEntity) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isAffected(BlockPos pos) {
        if (!(this.level() instanceof ServerLevel level)) return false;
        if (VeilHandler.isProtected(this.level(), pos)) return false;

        Set<DomainExpansionEntity> domains = VeilHandler.getDomains(level, pos);

        for (DomainExpansionEntity domain : domains) {
            if (domain == this) continue;

            return false;
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

    protected void doSureHitEffect(@NotNull LivingEntity owner) {
        for (LivingEntity entity : this.getAffected()) {
            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                ISorcererData data = cap.getSorcererData();

                if (data.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                    this.ability.onHitBlock(this, owner, entity.blockPosition());
                    continue;
                }
            }
            this.ability.onHitEntity(this, owner, entity, false);
        }
    }

    @Override
    public boolean checkSureHitEffect() {
        for (DomainExpansionEntity domain : VeilHandler.getDomains((ServerLevel) this.level(), this.getBounds())) {
            if (domain == this || domain instanceof ClosedDomainExpansionEntity closed && !closed.isInsideBarrier(this.blockPosition())) continue;

            if (this.shouldCollapse(domain.getStrength())) {
                this.discard();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (this.level().isClientSide) return;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        data.setBurnout(DomainExpansion.BURNOUT);

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
        }
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        if (!this.level().isClientSide) {
            if (this.checkSureHitEffect()) {
                this.doSureHitEffect(owner);
            }
        }
    }
}
