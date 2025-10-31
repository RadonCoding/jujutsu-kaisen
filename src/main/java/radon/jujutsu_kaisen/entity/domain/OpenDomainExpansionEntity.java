package radon.jujutsu_kaisen.entity.domain;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

public abstract class OpenDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_DIAMETER = SynchedEntityData.defineId(OpenDomainExpansionEntity.class, EntityDataSerializers.INT);
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

        this.entityData.set(DATA_DIAMETER, width);
        this.entityData.set(DATA_HEIGHT, height);
    }

    @Override
    public AABB getBounds() {
        double radius = (double) this.getDiameter() / 2;
        double height = (double) this.getHeight() / 2;
        return new AABB(
                this.getX() - radius, this.getY() - height, this.getZ() - radius,
                this.getX() + radius, this.getY() + height, this.getZ() + radius
        );
    }

    @Override
    public boolean isBarrier(BlockPos pos) {
        double dx = pos.getX() - this.getX();
        double dz = pos.getZ() - this.getZ();
        double dy = Math.abs(pos.getY() - this.getY());

        double radius = (double) this.getDiameter() / 2;
        double height = (double) this.getHeight() / 2;

        return dx * dx + dz * dz <= radius * radius && dy <= height;
    }
    @Override
    public boolean isInsideBarrier(BlockPos pos) {
        return this.isBarrier(pos);
    }

    @Override
    public boolean isBarrierOrInside(BlockPos pos) {
        return this.isBarrier(pos);
    }

    @Override
    public void push(@NotNull Entity pEntity) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    public int getDiameter() {
        return this.entityData.get(DATA_DIAMETER);
    }

    public int getHeight() {
        return this.entityData.get(DATA_HEIGHT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_DIAMETER, 0);
        pBuilder.define(DATA_HEIGHT, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("diameter", this.getDiameter());
        pCompound.putInt("height", this.getHeight());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_DIAMETER, pCompound.getInt("diameter"));
        this.entityData.set(DATA_HEIGHT, pCompound.getInt("height"));
    }

    @Override
    public void performAttack() {
        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        for (LivingEntity entity : this.getAffected()) {
            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                ISorcererData data = cap.getSorcererData();

                if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
                    this.ability.onHitNonLiving(this, owner, entity.blockPosition(), true, this.instant);
                    continue;
                }
            }
            this.ability.onHitLiving(this, owner, entity, this.instant);
        }

        AABB bounds = this.getBounds();

        BlockPos.betweenClosedStream(bounds).forEach(pos -> {
            if (!this.isAffected(pos)) return;

            this.ability.onHitNonLiving(this, owner, pos, false, this.instant);
        });
    }

    @Override
    public boolean canAttack() {
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
            PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(data.serializeNBT(player.registryAccess())));
        }
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        if (this.level().isClientSide) return;

        if (this.canAttack()) {
            this.performAttack();
        }
    }
}