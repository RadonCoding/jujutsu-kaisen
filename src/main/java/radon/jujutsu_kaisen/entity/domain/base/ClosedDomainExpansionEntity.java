package radon.jujutsu_kaisen.entity.domain.base;

import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.DomainHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.IBarrier;
import radon.jujutsu_kaisen.entity.IDomain;
import radon.jujutsu_kaisen.entity.ISimpleDomain;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.*;

public class ClosedDomainExpansionEntity extends DomainExpansionEntity {
    public static final int RADIUS = 20;

    public static TicketController CONTROLLER = new TicketController(JJKEntities.CLOSED_DOMAIN_EXPANSION.getId());

    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(ClosedDomainExpansionEntity.class, EntityDataSerializers.INT);

    private int total;

    @Nullable
    private Level inside;

    public ClosedDomainExpansionEntity(EntityType<? > pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ClosedDomainExpansionEntity(LivingEntity owner, DomainExpansion ability) {
        this(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), owner, ability);
    }

    public ClosedDomainExpansionEntity(EntityType<? > pType, LivingEntity owner, DomainExpansion ability) {
        super(pType, owner, ability);

        float yaw = RotationUtil.getTargetAdjustedYRot(owner);
        Vec3 direction = RotationUtil.calculateViewVector(0.0F, yaw);
        Vec3 behind = owner.position().subtract(0.0D, RADIUS, 0.0D).add(direction.scale(RADIUS - OFFSET));
        this.moveTo(behind.x, behind.y, behind.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        this.entityData.set(DATA_RADIUS, RADIUS);
    }

    @Override
    public boolean shouldCollapse(float strength) {
        int radius = this.getRadius();
        boolean completed = this.getTime() >= radius * 2;
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

        this.entityData.set(DATA_RADIUS, pCompound.getInt("radius"));
        this.total = pCompound.getInt("total");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("radius", this.getRadius());
        pCompound.putInt("total", this.total);
    }

    public int getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    @Override
    public boolean isInsideBarrier(BlockPos pos) {
        int radius = this.getRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < (radius - 1) * (radius - 1);
    }

    @Override
    public boolean isBarrier(BlockPos pos) {
        int radius = this.getRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < radius * radius;
    }

    private void createBlock(int delay, BlockPos pos, int radius, double distance) {
        if (distance > radius) return;

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

        Block block = distance < radius - 1 ? JJKBlocks.DOMAIN_AIR.get() : JJKBlocks.DOMAIN.get();

        if (distance >= radius - 1) {
            block = JJKBlocks.DOMAIN.get();
        }

        boolean success = owner.level().setBlock(pos, block.defaultBlockState(),
                Block.UPDATE_CLIENTS);

        if (distance >= radius - 1 && success) this.total++;

        if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity be) {
            be.create(this.uuid, delay, state, saved);
        }
    }

    private void createOutsideBarrier() {
        this.total = 0;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        int radius = this.getRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        Vec3 direction = this.getLookAngle();
        Vec3 behind = this.position().subtract(direction.scale(radius - OFFSET)).add(0.0D, radius, 0.0D);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance > radius) continue;

                    BlockPos pos = center.offset(x, y, z);

                    int delay = (int) Math.round(pos.getCenter().distanceTo(behind)) / 2;

                    IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                    if (cap == null) return;

                    if (delay == 0) {
                        this.createBlock(radius - delay, pos, radius, distance);
                    } else {
                        IAbilityData data = cap.getAbilityData();
                        data.delayTickEvent(() -> this.createBlock(radius - delay, pos, radius, distance), delay);
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
        int radius = this.getRadius() * 2;
        return EntityDimensions.fixed(radius, radius);
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

        int radius = this.getRadius();
        BlockPos center = this.blockPosition().offset(0, radius / 2, 0);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius - 1) {
                        BlockPos pos = center.offset(x, y, z);
                        this.ability.onHitBlock(this, owner, pos, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean checkSureHitEffect() {
        int radius = this.getRadius();
        boolean completed = this.getTime() >= radius * 2;

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
        int radius = this.getRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        int count = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius && distance >= radius - 1) {
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

        int radius = this.getRadius();
        boolean completed = this.getTime() >= radius * 2;

        if (this.checkSureHitEffect()) {
            this.doSureHitEffect(owner);
        }

        if (this.getTime() - 1 == 0) this.createOutsideBarrier();
        if (this.getTime() == radius * 2) this.createInsideBarrier();

        if (completed) {
            if (this.getTime() % 20 == 0) {
                this.check();
            }

            if (this.inside instanceof ServerLevel level) {
                IDomainData data = this.inside.getData(JJKAttachmentTypes.DOMAIN);
                data.update(this);

                for (Entity entity : this.level().getEntities(this, this.getBounds(), entity -> this.isInsideBarrier(entity.blockPosition()))) {
                    entity.teleportTo(level, entity.getX(), entity.getY(), entity.getZ(), Set.of(), 0.0F, 0.0F);
                }
            }
        }
    }

    @Override
    public @Nullable Entity getCenter() {
        return this.getOwner();
    }

    @Nullable
    @Override
    public Level getInside() {
        return this.inside;
    }
}
