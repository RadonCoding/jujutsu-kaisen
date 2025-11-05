package radon.jujutsu_kaisen.entity.domain;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.IClosedDomain;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.base.ITemporaryBlockEntity;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.IDomain;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.*;

public class ClosedDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(ClosedDomainExpansionEntity.class, EntityDataSerializers.INT);

    private final Map<UUID, Vec3> positions = new HashMap<>();

    public ClosedDomainExpansionEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ClosedDomainExpansionEntity(LivingEntity owner, DomainExpansion ability) {
        this(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), owner, ability);
    }

    public ClosedDomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability) {
        super(pType, owner, ability);

        int radius = ConfigHolder.SERVER.domainRadius.getAsInt();

        float yaw = RotationUtil.getTargetAdjustedYRot(owner);
        Vec3 direction = RotationUtil.calculateViewVector(0.0F, yaw);
        Vec3 behind = owner.position().subtract(0.0D, radius, 0.0D).add(direction.scale(radius - OFFSET));
        this.moveTo(behind.x, behind.y, behind.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        this.entityData.set(DATA_RADIUS, radius);
    }

    private boolean isCompleted() {
        int radius = this.getRadius();
        return this.getTime() >= radius * 2;
    }

    @Override
    public boolean isReadyToCollapse() {
        return this.isCompleted();
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

        for (Tag tag : pCompound.getList("positions", Tag.TAG_COMPOUND)) {
            CompoundTag nbt = (CompoundTag) tag;
            this.positions.put(nbt.getUUID("identifier"), new Vec3(nbt.getDouble("x"),
                    nbt.getDouble("y"), nbt.getDouble("z")));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("radius", this.getRadius());

        ListTag positionsTag = new ListTag();

        for (Map.Entry<UUID, Vec3> entry : this.positions.entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("identifier", entry.getKey());

            Vec3 position = entry.getValue();
            nbt.putDouble("x", position.x);
            nbt.putDouble("y", position.y);
            nbt.putDouble("z", position.z);

            positionsTag.add(nbt);
        }
        pCompound.put("positions", positionsTag);
    }

    public int getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    @Override
    public boolean isBarrier(BlockPos pos) {
        int radius = this.getRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return Math.sqrt(relative.distSqr(Vec3i.ZERO)) >= radius - 2 && Math.sqrt(relative.distSqr(Vec3i.ZERO)) <= radius;
    }

    @Override
    public boolean isInsideBarrier(BlockPos pos) {
        int radius = this.getRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return Math.sqrt(relative.distSqr(Vec3i.ZERO)) < radius - 2;
    }

    @Override
    public boolean isBarrierOrInside(BlockPos pos) {
        return this.isBarrier(pos) || this.isInsideBarrier(pos);
    }

    protected void createBlock(int delay, BlockPos pos, int radius, double distance) {
        if (distance > radius) return;

        if (!this.level().isInWorldBounds(pos)) return;

        BlockState previous = this.level().getBlockState(pos);

        if (previous.is(Blocks.BEDROCK)) return;

        if (this.isRemoved()) return;

        if (this.isOwnedByNonDomain(pos)) return;

        BlockEntity existing = this.level().getBlockEntity(pos);

        CompoundTag saved = null;

        if (existing != null && !(existing instanceof ITemporaryBlockEntity)) {
            saved = existing.saveWithFullMetadata(this.registryAccess());
        }

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        IClosedDomain closed = ((IClosedDomain) this.ability);
        List<Block> blocks = closed.getBlocks();

        Block block;

        if (distance >= radius - 1) block = JJKBlocks.DOMAIN.get();
        else if (distance >= radius - 2) block = JJKBlocks.DOMAIN_SKY.get();
        else if (pos.getY() < center.getY()) block = blocks.get(HelperMethods.RANDOM.nextInt(blocks.size()));
        else block = JJKBlocks.DOMAIN_AIR.get();

        this.level().setBlock(pos, block.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);

        if (existing instanceof ITemporaryBlockEntity tmp) {
            previous = tmp.getOriginal();
            saved = tmp.getSaved();
        }

        if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity be) {
            be.create(this.uuid, delay, previous, saved);
        }
    }

    private void createBarrier(boolean instant) {
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

                    if (instant) {
                        this.createBlock(radius - delay, pos, radius, distance);
                    } else {
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
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        int physicalDiameter = this.getRadius() * 2;
        return EntityDimensions.fixed(physicalDiameter, physicalDiameter);
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

        int virtualRadius = ConfigHolder.SERVER.domainRadius.getAsInt();
        BlockPos center = BlockPos.ZERO.offset(0, virtualRadius / 2, 0);

        for (int x = -virtualRadius; x <= virtualRadius; x++) {
            for (int y = -virtualRadius; y <= virtualRadius; y++) {
                for (int z = -virtualRadius; z <= virtualRadius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < virtualRadius - 1) {
                        BlockPos pos = center.offset(x, y, z);
                        this.ability.onHitNonLiving(this, owner, pos, false, this.instant);
                    }
                }
            }
        }
    }

    @Override
    public boolean canAttack() {
        if (!this.isCompleted()) return false;

        Set<IDomain> domains = VeilHandler.getDomains((ServerLevel) this.level(), this.getBounds());

        for (IDomain domain : domains) {
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
    }

    private void check() {
        int radius = this.getRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        Map<Direction, Integer> counts = new EnumMap<>(Direction.class);
        Map<Direction, Integer> totals = new EnumMap<>(Direction.class);

        for (Direction direction : Direction.values()) {
            counts.put(direction, 0);
            totals.put(direction, 0);
        }

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius - 2 || distance > radius) continue;

                    BlockPos pos = center.offset(x, y, z);

                    boolean intact = this.level().getBlockEntity(pos) instanceof DomainBlockEntity;

                    if (y > 0) {
                        totals.merge(Direction.UP, 1, Integer::sum);

                        if (intact) counts.merge(Direction.UP, 1, Integer::sum);
                    }
                    if (y < 0) {
                        totals.merge(Direction.DOWN, 1, Integer::sum);

                        if (intact) counts.merge(Direction.DOWN, 1, Integer::sum);
                    }
                    if (z > 0) {
                        totals.merge(Direction.SOUTH, 1, Integer::sum);

                        if (intact) counts.merge(Direction.SOUTH, 1, Integer::sum);
                    }
                    if (z < 0) {
                        totals.merge(Direction.NORTH, 1, Integer::sum);

                        if (intact) counts.merge(Direction.NORTH, 1, Integer::sum);
                    }
                    if (x > 0) {
                        totals.merge(Direction.EAST, 1, Integer::sum);

                        if (intact) counts.merge(Direction.EAST, 1, Integer::sum);
                    }
                    if (x < 0) {
                        totals.merge(Direction.WEST, 1, Integer::sum);

                        if (intact) counts.merge(Direction.WEST, 1, Integer::sum);
                    }
                }
            }
        }

        for (Direction direction : Direction.values()) {
            int total = totals.get(direction);

            if (total == 0) continue;

            if (counts.get(direction) / (float) total < 0.75F) {
                this.discard();
                return;
            }
        }
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);

        Set<IDomain> domains = VeilHandler.getDomains((ServerLevel) this.level(), this.getBounds());

        for (IDomain domain : domains) {
            if (domain == this || !(domain instanceof ClosedDomainExpansionEntity closed)) continue;

            closed.createBarrier(true);
        }

        for (Map.Entry<UUID, Vec3> entry : this.positions.entrySet()) {
            UUID identifier = entry.getKey();

            Entity entity = ((ServerLevel) this.level()).getEntity(identifier);

            if (entity == null) continue;

            if (!this.isInsideBarrier(entity.blockPosition())) continue;

            Vec3 pos = entry.getValue();

            entity.teleportTo(pos.x, pos.y, pos.z);
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

        if (this.getTime() <= radius * 2) {
            BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds(),
                    entity -> this.isInsideBarrier(entity.blockPosition()))) {
                if (!this.positions.containsKey(entity.getUUID())) {
                    this.positions.put(entity.getUUID(), entity.position());
                }
                if (entity.getY() < center.getY()) {
                    entity.teleportTo(entity.getX(), center.getY(), entity.getZ());
                }
            }
        }

        if (this.canAttack()) {
            this.performAttack();
        }

        if (this.isCompleted()) {
            if (this.getTime() % 20 == 0) {
                this.check();
            }
        }

        if (this.getTime() - 1 == 0) {
            this.createBarrier(false);
        } else if (this.isCompleted() && !this.isInsideBarrier(owner.blockPosition())) {
            this.discard();
        }
    }
}
