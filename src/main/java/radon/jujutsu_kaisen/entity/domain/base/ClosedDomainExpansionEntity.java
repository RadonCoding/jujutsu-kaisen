package radon.jujutsu_kaisen.entity.domain.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
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
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.block.entity.base.ITemporaryBlock;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.IBarrier;
import radon.jujutsu_kaisen.entity.base.IDomain;
import radon.jujutsu_kaisen.entity.base.ISimpleDomain;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.*;

public class ClosedDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(ClosedDomainExpansionEntity.class, EntityDataSerializers.INT);

    private int total;

    private final Map<UUID, Vec3> positions = new HashMap<>();

    public ClosedDomainExpansionEntity(EntityType<? > pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ClosedDomainExpansionEntity(LivingEntity owner, DomainExpansion ability, int radius) {
        this(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), owner, ability, radius);
    }

    public ClosedDomainExpansionEntity(EntityType<? > pType, LivingEntity owner, DomainExpansion ability, int radius) {
        super(pType, owner, ability);

        float yaw = RotationUtil.getTargetAdjustedYRot(owner);
        Vec3 direction = RotationUtil.calculateViewVector(0.0F, yaw);
        Vec3 behind = owner.position().subtract(0.0D, radius, 0.0D).add(direction.scale(radius - OFFSET));
        this.moveTo(behind.x, behind.y, behind.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        this.entityData.set(DATA_RADIUS, radius);
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
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_RADIUS, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_RADIUS, pCompound.getInt("radius"));
        this.total = pCompound.getInt("total");

        for (Tag key : pCompound.getList("positions", Tag.TAG_COMPOUND)) {
            CompoundTag nbt = (CompoundTag) key;
            this.positions.put(nbt.getUUID("identifier"), new Vec3(nbt.getDouble("pos_x"),
                    nbt.getDouble("pos_y"), nbt.getDouble("pos_z")));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("radius", this.getRadius());
        pCompound.putInt("total", this.total);

        ListTag positionsTag = new ListTag();

        for (Map.Entry<UUID, Vec3> entry : this.positions.entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("identifier", entry.getKey());

            Vec3 position = entry.getValue();
            nbt.putDouble("pos_x", position.x);
            nbt.putDouble("pos_y", position.y);
            nbt.putDouble("pos_z", position.z);

            positionsTag.add(nbt);
        }
        pCompound.put("positions", positionsTag);
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

    protected void createBlock(int delay, BlockPos pos, int radius, double distance) {
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

        if (existing instanceof ITemporaryBlock tmp) {
            state = tmp.getOriginal();
        } else if (existing != null) {
            saved = existing.saveWithFullMetadata();
        }

        DomainExpansion.IClosedDomain domain = ((DomainExpansion.IClosedDomain) this.ability);
        List<Block> blocks = domain.getBlocks();
        List<Block> fill = domain.getFillBlocks();
        List<Block> floor = domain.getFloorBlocks();
        List<Block> decoration = domain.getDecorationBlocks();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        Block block;

        if (distance >= radius - 1) {
            block = JJKBlocks.DOMAIN.get();
        } else {
            if (distance >= radius - 2) {
                block = blocks.get(this.random.nextInt(blocks.size()));
            } else if (pos.getY() < center.getY()) {
                block = floor.isEmpty() ? fill.get(this.random.nextInt(fill.size())) : floor.get(this.random.nextInt(floor.size()));
            } else if (!decoration.isEmpty() && pos.getY() == center.getY()) {
                block = decoration.get(this.random.nextInt(decoration.size()));
            } else {
                block = JJKBlocks.DOMAIN_AIR.get();
            }
        }

        // We don't want to destroy the barrier of other domains :P
        if (existing instanceof DomainBlockEntity be) {
            UUID identifier = be.getIdentifier();

            if (identifier != null && ((ServerLevel) this.level()).getEntity(identifier) instanceof DomainExpansionEntity) {
                if (block == JJKBlocks.DOMAIN_AIR.get()) return;
            }
        }

        owner.level().removeBlockEntity(pos);

        boolean success = owner.level().setBlock(pos, block.defaultBlockState(),
                Block.UPDATE_CLIENTS);

        if (distance >= radius - 1 && success) this.total++;

        if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity be) {
            be.create(this.uuid, delay, state, saved);
        }
    }

    protected void createBarrier(boolean instant) {
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
        int radius = this.getRadius() * 2;
        return EntityDimensions.fixed(radius, radius);
    }

    protected void doSureHitEffect(@NotNull LivingEntity owner) {
        for (LivingEntity entity : this.getAffected()) {
            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                ISorcererData data = cap.getSorcererData();

                if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
                    this.ability.onHitBlock(this, owner, entity.blockPosition());
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
                        this.ability.onHitBlock(this, owner, pos);
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
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
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
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);

        Set<IBarrier> barriers = VeilHandler.getBarriers((ServerLevel) this.level(), this.getBounds());

        for (IBarrier barrier : barriers) {
            if (!(barrier instanceof IDomain domain)) continue;
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
        boolean completed = this.getTime() >= radius * 2;

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

        if (this.checkSureHitEffect()) {
            this.doSureHitEffect(owner);
        }

        if (completed) {
            if (this.getTime() % 20 == 0) {
                this.check();
            }
        }

        ParticleOptions particle = ((DomainExpansion.IClosedDomain) this.ability).getEnvironmentParticle();

        if (particle != null) {
            AABB bounds = this.getBounds();

            for (BlockPos pos : BlockPos.randomBetweenClosed(this.random, 16, (int) bounds.minX, (int) bounds.minY, (int) bounds.minZ,
                    (int) bounds.maxX, (int) bounds.maxY, (int) bounds.maxZ)) {
                if (!this.isInsideBarrier(pos)) continue;
                Vec3 center = pos.getCenter();
                ((ServerLevel) this.level()).sendParticles(particle, center.x, center.y, center.z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }

        if (this.getTime() - 1 == 0) {
            this.createBarrier(false);
        } else if (completed && !this.isInsideBarrier(owner.blockPosition())) {
            this.discard();
        }
    }
}
