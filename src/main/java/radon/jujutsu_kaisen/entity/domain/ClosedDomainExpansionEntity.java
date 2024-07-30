package radon.jujutsu_kaisen.entity.domain;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.IBarrier;
import radon.jujutsu_kaisen.entity.IDomain;
import radon.jujutsu_kaisen.entity.ISimpleDomain;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.Set;

public class ClosedDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(ClosedDomainExpansionEntity.class, EntityDataSerializers.INT);
    public static TicketController CONTROLLER = new TicketController(JJKEntities.CLOSED_DOMAIN_EXPANSION.getId());
    private int total;

    @Nullable
    private Level inside;

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

        int outsideRadius = data.getDomainSize();

        float yaw = RotationUtil.getTargetAdjustedYRot(owner);
        Vec3 direction = RotationUtil.calculateViewVector(0.0F, yaw);
        Vec3 behind = owner.position().subtract(0.0D, outsideRadius, 0.0D).add(direction.scale(outsideRadius - OFFSET));
        this.moveTo(behind.x, behind.y, behind.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        this.entityData.set(DATA_RADIUS, outsideRadius);
    }

    @Override
    public boolean shouldCollapse(float strength) {
        int outsideRadius = this.getOutsideRadius();
        boolean completed = this.getTime() >= outsideRadius * 2;
        return completed && super.shouldCollapse(strength);
    }

    @Override
    public AABB getBounds() {
        return this.getBoundingBox();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_RADIUS, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_RADIUS, pCompound.getInt("outsideRadius"));
        this.total = pCompound.getInt("total");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("outsideRadius", this.getOutsideRadius());
        pCompound.putInt("total", this.total);
    }

    public int getOutsideRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    @Override
    public boolean isInsideBarrier(BlockPos pos) {
        int outsideRadius = this.getOutsideRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, outsideRadius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < (outsideRadius - 1) * (outsideRadius - 1);
    }

    @Override
    public boolean isBarrier(BlockPos pos) {
        int outsideRadius = this.getOutsideRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, outsideRadius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < outsideRadius * outsideRadius;
    }

    private void createBlock(int delay, BlockPos pos, int outsideRadius, double distance) {
        if (distance > outsideRadius) return;

        if (!this.level().isInWorldBounds(pos)) return;

        BlockState state = this.level().getBlockState(pos);

        if (state.is(Blocks.BEDROCK)) return;

        if (this.isRemoved()) return;

        if (this.isOwnedByNonDomain(pos)) return;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        BlockEntity existing = this.level().getBlockEntity(pos);

        CompoundTag saved = null;

        if (existing != null) {
            saved = existing.saveWithFullMetadata(this.registryAccess());
        }

        Block block = state.getCollisionShape(this.level(), pos).isEmpty() ? JJKBlocks.DOMAIN_AIR.get() : JJKBlocks.DOMAIN.get();

        if (distance >= outsideRadius - 1) {
            block = JJKBlocks.DOMAIN.get();
        }

        boolean success = owner.level().setBlock(pos, block.defaultBlockState(),
                Block.UPDATE_CLIENTS);

        if (distance >= outsideRadius - 1 && success) this.total++;

        if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity be) {
            be.create(this.uuid, delay, state, saved);
        }
    }

    private void createOutsideBarrier() {
        this.total = 0;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        int outsideRadius = this.getOutsideRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, outsideRadius, 0.0D));

        Vec3 direction = this.getLookAngle();
        Vec3 behind = this.position().subtract(direction.scale(outsideRadius - OFFSET)).add(0.0D, outsideRadius, 0.0D);

        for (int x = -outsideRadius; x <= outsideRadius; x++) {
            for (int y = -outsideRadius; y <= outsideRadius; y++) {
                for (int z = -outsideRadius; z <= outsideRadius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance > outsideRadius) continue;

                    BlockPos pos = center.offset(x, y, z);

                    int delay = (int) Math.round(pos.getCenter().distanceTo(behind)) / 2;

                    IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                    if (cap == null) return;

                    if (delay == 0) {
                        this.createBlock(outsideRadius - delay, pos, outsideRadius, distance);
                    } else {
                        IAbilityData data = cap.getAbilityData();
                        data.delayTickEvent(() -> this.createBlock(outsideRadius - delay, pos, outsideRadius, distance), delay);
                    }
                }
            }
        }
    }

    private void createInsideBarrier() {
        this.inside = DomainHandler.getOrCreateInside((ServerLevel) this.level(), this);

        if (this.inside == null) return;

        IDomainData data = this.inside.getData(JJKAttachmentTypes.DOMAIN);
        data.update(this);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        int outsideRadius = this.getOutsideRadius() * 2;
        return EntityDimensions.fixed(outsideRadius, outsideRadius);
    }

    private void doSureHitEffect(@NotNull LivingEntity owner) {
        if (this.inside == null) return;

        for (LivingEntity entity : this.getAffected(this.inside)) {
            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                ISorcererData data = cap.getSorcererData();

                if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
                    this.ability.onHitBlock(this, owner, entity.blockPosition(), false);
                    continue;
                }
            }
            this.ability.onHitEntity(this, owner, entity, false);
        }

        int outsideRadius = this.getOutsideRadius();
        BlockPos center = this.blockPosition().offset(0, outsideRadius / 2, 0);

        for (int x = -outsideRadius; x <= outsideRadius; x++) {
            for (int y = -outsideRadius; y <= outsideRadius; y++) {
                for (int z = -outsideRadius; z <= outsideRadius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < outsideRadius - 1) {
                        BlockPos pos = center.offset(x, y, z);
                        this.ability.onHitBlock(this, owner, pos, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean checkSureHitEffect() {
        int outsideRadius = this.getOutsideRadius();
        boolean completed = this.getTime() >= outsideRadius * 2;

        if (!completed) return false;

        Set<IBarrier> barriers = VeilHandler.getBarriers((ServerLevel) this.level(), this.getBounds());

        for (IBarrier barrier : barriers) {
            if (!(barrier instanceof IDomain domain) || barrier instanceof ISimpleDomain) continue;
            if (domain == this) continue;

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
        int outsideRadius = this.getOutsideRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, outsideRadius, 0.0D));

        int count = 0;

        for (int x = -outsideRadius; x <= outsideRadius; x++) {
            for (int y = -outsideRadius; y <= outsideRadius; y++) {
                for (int z = -outsideRadius; z <= outsideRadius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < outsideRadius && distance >= outsideRadius - 1) {
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

        int outsideRadius = this.getOutsideRadius();
        boolean completed = this.getTime() >= outsideRadius * 2;

        if (this.checkSureHitEffect()) {
            this.doSureHitEffect(owner);
        }

        if (this.getTime() - 1 == 0) this.createOutsideBarrier();
        if (this.getTime() == outsideRadius * 2) this.createInsideBarrier();

        if (completed) {
            if (this.getTime() % 20 == 0) {
                this.check();
            }

            if (this.inside instanceof ServerLevel level) {
                IDomainData data = this.inside.getData(JJKAttachmentTypes.DOMAIN);
                data.update(this);

                int insideRadius = ConfigHolder.SERVER.domainSize.getAsInt();
                int diameter = (insideRadius - 1) * 2;

                for (Entity entity : this.level().getEntities(this, this.getBounds(), entity -> this.isInsideBarrier(entity.blockPosition()))) {
                    data.addSpawn(entity.getUUID(), entity.position());

                    Vec3 distance = entity.position().subtract(this.position());

                    entity.teleportTo(level, Math.min(diameter, distance.x), insideRadius, Math.min(diameter, distance.z), Set.of(),
                            entity.getYRot(), entity.getXRot());
                }
            }
        }
    }

    @Nullable
    @Override
    public Level getInside() {
        return this.inside;
    }
}
