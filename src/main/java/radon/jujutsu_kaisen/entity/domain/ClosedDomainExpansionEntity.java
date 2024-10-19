package radon.jujutsu_kaisen.entity.domain;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.DomainHandler;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.base.ITemporaryBlockEntity;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.domain.DomainInfo;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.Set;

public class ClosedDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(ClosedDomainExpansionEntity.class, EntityDataSerializers.INT);
    public static TicketController CONTROLLER = new TicketController(JJKEntities.CLOSED_DOMAIN_EXPANSION.getId());
    private int total;

    @Nullable
    private Level virtual;

    public ClosedDomainExpansionEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ClosedDomainExpansionEntity(LivingEntity owner, DomainExpansion ability) {
        this(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), owner, ability);
    }

    public ClosedDomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability) {
        super(pType, owner, ability);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        int physicalRadius = data.getDomainSize();

        float yaw = RotationUtil.getTargetAdjustedYRot(owner);
        Vec3 direction = RotationUtil.calculateViewVector(0.0F, yaw);
        Vec3 behind = owner.position().subtract(0.0D, physicalRadius, 0.0D).add(direction.scale(physicalRadius - OFFSET));
        this.moveTo(behind.x, behind.y, behind.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        this.entityData.set(DATA_RADIUS, physicalRadius);
    }

    @Override
    public boolean shouldCollapse(float strength) {
        int physicalRadius = this.getPhysicalRadius();
        boolean completed = this.getTime() >= physicalRadius * 2;
        return completed && super.shouldCollapse(strength);
    }

    @Override
    public AABB getPhysicalBounds() {
        return this.getBoundingBox();
    }

    @Override
    public AABB getVirtualBounds() {
        int virtualRadius = ConfigHolder.SERVER.virtualDomainRadius.getAsInt();
        int virtualDiameter = virtualRadius * 2;
        return AABB.ofSize(new Vec3(0.0D, virtualRadius, 0.0D), virtualDiameter, virtualDiameter, virtualDiameter);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_RADIUS, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_RADIUS, pCompound.getInt("physicalRadius"));
        this.total = pCompound.getInt("total");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("physicalRadius", this.getPhysicalRadius());
        pCompound.putInt("total", this.total);
    }

    public int getPhysicalRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    @Override
    public boolean isInsidePhysicalBarrier(BlockPos pos) {
        int physicalRadius = this.getPhysicalRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, physicalRadius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return Math.sqrt(relative.distSqr(Vec3i.ZERO)) < physicalRadius - 1;
    }

    @Override
    public boolean isPhysicalBarrier(BlockPos pos) {
        int physicalRadius = this.getPhysicalRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, physicalRadius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return Math.sqrt(relative.distSqr(Vec3i.ZERO)) < physicalRadius;
    }

    @Override
    public boolean isInsideVirtualBarrier(BlockPos pos) {
        if (this.instant) return this.isInsidePhysicalBarrier(pos);

        int virtualRadius = ConfigHolder.SERVER.virtualDomainRadius.getAsInt();
        BlockPos center = new BlockPos(0, virtualRadius, 0);
        BlockPos relative = pos.subtract(center);
        return Math.sqrt(relative.distSqr(Vec3i.ZERO)) < virtualRadius - 1;
    }

    private void createBlock(int delay, BlockPos pos, int radius, double distance) {
        if (distance > radius) return;

        if (!this.level().isInWorldBounds(pos)) return;

        BlockState state = this.level().getBlockState(pos);

        if (state.is(Blocks.BEDROCK)) return;

        if (this.isRemoved()) return;

        if (this.isOwnedByNonDomain(pos)) return;

        BlockEntity existing = this.level().getBlockEntity(pos);

        CompoundTag saved = null;

        if (existing != null && !(existing instanceof ITemporaryBlockEntity)) {
            saved = existing.saveWithFullMetadata(this.registryAccess());
        }

        Block block = state.getCollisionShape(this.level(), pos).isEmpty() ? JJKBlocks.DOMAIN_AIR.get() : JJKBlocks.DOMAIN_SKY.get();

        if (distance >= radius - 1) block = JJKBlocks.DOMAIN.get();
        else if (distance >= radius - 2) block = JJKBlocks.DOMAIN_SKY.get();

        boolean success = this.level().setBlock(pos, block.defaultBlockState(),
                Block.UPDATE_CLIENTS);

        if (distance >= radius - 1 && success) this.total++;

        if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity be) {
            be.create(this.uuid, delay, state, saved);
        }
    }

    private void createPhysicalBarrier() {
        this.total = 0;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        int physicalRadius = this.getPhysicalRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, physicalRadius, 0.0D));

        Vec3 direction = this.getLookAngle();
        Vec3 behind = this.position().subtract(direction.scale(physicalRadius - OFFSET)).add(0.0D, physicalRadius, 0.0D);

        for (int x = -physicalRadius; x <= physicalRadius; x++) {
            for (int y = -physicalRadius; y <= physicalRadius; y++) {
                for (int z = -physicalRadius; z <= physicalRadius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance > physicalRadius) continue;

                    BlockPos pos = center.offset(x, y, z);

                    int delay = (int) Math.round(pos.getCenter().distanceTo(behind)) / 2;

                    IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                    if (cap == null) return;

                    if (delay == 0) {
                        this.createBlock(physicalRadius - delay, pos, physicalRadius, distance);
                    } else {
                        IAbilityData data = cap.getAbilityData();
                        data.delayTickEvent(() -> this.createBlock(physicalRadius - delay, pos, physicalRadius, distance), delay);
                    }
                }
            }
        }
    }

    private void createVirtualBarrier() {
        this.virtual = DomainHandler.getOrCreateInside((ServerLevel) this.level(), this);

        if (this.virtual == null) return;

        IDomainData data = this.virtual.getData(JJKAttachmentTypes.DOMAIN);
        data.update(this);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        int physicalDiameter = this.getPhysicalRadius() * 2;
        return EntityDimensions.fixed(physicalDiameter, physicalDiameter);
    }

    @Override
    public void doSureHitEffect(LivingEntity owner) {
        Level barrier = this.instant ? this.level() : this.virtual;

        if (barrier == null) return;

        for (LivingEntity entity : this.getAffected(barrier)) {
            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                ISorcererData data = cap.getSorcererData();

                if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
                    this.ability.onHitBlock(this.virtual, this, owner, entity.blockPosition(), this.instant);
                    continue;
                }
            }
            this.ability.onHitEntity(this, owner, entity, this.instant);
        }

        if (this.virtual != null) {
            int virtualRadius = ConfigHolder.SERVER.virtualDomainRadius.getAsInt();
            BlockPos center = BlockPos.ZERO.offset(0, virtualRadius / 2, 0);

            for (int x = -virtualRadius; x <= virtualRadius; x++) {
                for (int y = -virtualRadius; y <= virtualRadius; y++) {
                    for (int z = -virtualRadius; z <= virtualRadius; z++) {
                        double distance = Math.sqrt(x * x + y * y + z * z);

                        if (distance < virtualRadius - 1) {
                            BlockPos pos = center.offset(x, y, z);
                            this.ability.onHitBlock(this.virtual, this, owner, pos, this.instant);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean checkSureHitEffect() {
        if (this.instant) return true;

        if (this.virtual == null) return false;

        IDomainData data = this.virtual.getData(JJKAttachmentTypes.DOMAIN);
        Set<DomainInfo> domains = data.getDomains();
        return domains.size() == 1;
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
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.refreshDimensions();

        if (this.level() instanceof ServerLevel level) {
            ChunkPos pos = this.chunkPosition();
            CONTROLLER.forceChunk(level, this, pos.x, pos.z, true, true);
        }
    }

    private void check() {
        int physicalRadius = this.getPhysicalRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, physicalRadius, 0.0D));

        int count = 0;

        for (int x = -physicalRadius; x <= physicalRadius; x++) {
            for (int y = -physicalRadius; y <= physicalRadius; y++) {
                for (int z = -physicalRadius; z <= physicalRadius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < physicalRadius && distance >= physicalRadius - 1) {
                        BlockPos pos = center.offset(x, y, z);

                        if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity) count++;
                    }
                }
            }
        }

        if ((float) count / this.total < 0.75F) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        if (this.level().isClientSide) return;

        int physicalRadius = this.getPhysicalRadius();
        boolean completed = this.getTime() >= physicalRadius * 2;

        if (this.checkSureHitEffect()) {
            this.doSureHitEffect(owner);
        }

        if (this.getTime() - 1 == 0) {
            this.createPhysicalBarrier();
        }

        if (!this.instant && this.getTime() == physicalRadius * 2) {
            this.createVirtualBarrier();
        }

        if (completed) {
            if (this.virtual instanceof ServerLevel level) {
                IDomainData data = this.virtual.getData(JJKAttachmentTypes.DOMAIN);
                data.update(this);

                int virtualRadius = ConfigHolder.SERVER.virtualDomainRadius.getAsInt();

                for (Entity entity : this.level().getEntities(this, this.getPhysicalBounds(), entity -> this.isInsidePhysicalBarrier(entity.blockPosition()))) {
                    data.addSpawn(entity.getUUID(), entity.position());

                    Vec3 distance = entity.position().subtract(this.position())
                            .scale(1.0D / physicalRadius)
                            .scale(virtualRadius);

                    entity.teleportTo(level, distance.x, virtualRadius, distance.z, Set.of(),
                            entity.getYRot(), entity.getXRot());
                }
            }

            if (this.getTime() % 20 == 0) {
                this.check();
            }
        }
    }

    @Nullable
    @Override
    public Level getVirtual() {
        return this.virtual;
    }
}
