package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.DomainBlock;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.*;

public class ClosedDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(ClosedDomainExpansionEntity.class, EntityDataSerializers.INT);
    private static final float STRENGTH = 100.0F;

    private final Map<BlockPos, BlockState> blocks = new HashMap<>();

    private BlockState block;


    public ClosedDomainExpansionEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ClosedDomainExpansionEntity(LivingEntity owner, DomainExpansion ability, BlockState block, int radius, int duration) {
        super(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), owner, ability, duration);

        this.moveTo(owner.getX(), owner.getY() - (double) (radius / 2), owner.getZ());

        this.block = block;

        this.entityData.set(DATA_RADIUS, radius);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);

            if (attribute != null) {
                attribute.setBaseValue(STRENGTH * cap.getGrade().getPower());
                this.setHealth(this.getMaxHealth());
            }
        });
    }

    @Override
    public AABB getBounds() {
        int radius = this.getRadius();
        return new AABB(this.getX() - radius, this.getY(), this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_RADIUS, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.block = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), pCompound.getCompound("block"));

        this.entityData.set(DATA_RADIUS, pCompound.getInt("radius"));

        for (Tag key : pCompound.getList("blocks", Tag.TAG_COMPOUND)) {
            CompoundTag block = (CompoundTag) key;
            this.blocks.put(NbtUtils.readBlockPos(block.getCompound("pos")),
                    NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), block.getCompound("state")));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.put("block", NbtUtils.writeBlockState(this.block));
        pCompound.putInt("radius", this.getRadius());

        ListTag blocksTag = new ListTag();

        for (Map.Entry<BlockPos, BlockState> entry : this.blocks.entrySet()) {
            CompoundTag block = new CompoundTag();
            block.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
            block.put("state", NbtUtils.writeBlockState(entry.getValue()));
            blocksTag.add(block);
        }
        pCompound.put("blocks", blocksTag);
    }

    private int getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    private List<DomainExpansionEntity> getDomainsInside() {
        List<DomainExpansionEntity> entities = new ArrayList<>();

        AABB bounds = this.getBounds();

        for (DomainExpansionEntity entity : this.level.getEntitiesOfClass(DomainExpansionEntity.class, bounds)) {
            if (this.isInsideBarrier(entity)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean isInsideBarrier(Entity entity) {
        int radius = this.getRadius();
        BlockPos center = this.blockPosition().offset(0, radius / 2, 0);
        BlockPos relative = entity.blockPosition().subtract(center);
        return relative.distSqr(Vec3i.ZERO) < radius * radius;
    }

    private void createBarrier(Entity owner) {
        if (this.block == null) return;

        int radius = this.getRadius();
        BlockPos center = this.blockPosition().offset(0, radius / 2, 0);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius && distance >= radius - 1) {
                        BlockPos pos = center.offset(x, y, z);
                        BlockState state = this.level.getBlockState(pos);

                        if (state.getBlock() instanceof DomainBlock) continue;

                        this.blocks.put(pos, state);

                        int delay = radius - (pos.getY() - center.getY());

                        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                            cap.delayTickEvent((ownerClone) -> {
                                if (!this.isRemoved()) {
                                    ownerClone.level.setBlockAndUpdate(pos, this.block);
                                }
                            }, delay);
                        });
                    }
                }
            }
        }
    }

    @Override
    public void warn() {
        LivingEntity owner = this.getOwner();

        if (owner != null) {
            AABB bounds = this.getBounds();

            for (Entity entity : this.level.getEntities(this, bounds, this::isAffected)) {
                entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.onInsideDomain(this));
            }
        }
        this.warned = true;
    }

    public void destroyBarrier() {
        Iterator<Map.Entry<BlockPos, BlockState>> iter = this.blocks.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<BlockPos, BlockState> entry = iter.next();

            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            if (state.getBlock() instanceof DomainBlock) continue;

            if (state.isAir()) {
                this.level.destroyBlock(pos, false);
            } else {
                this.level.setBlockAndUpdate(pos, state);
            }
            iter.remove();
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        int radius = this.getRadius() * 2;
        return EntityDimensions.fixed(radius, radius);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void aiStep() {}

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return this.isAlive();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        Entity entity = pSource.getDirectEntity();

        int radius = this.getRadius();
        boolean isCompleted = this.getTime() >= radius * 2;

        if (!isCompleted || entity != null && this.isInsideBarrier(entity)) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    private void doSureHitEffect(@NotNull LivingEntity owner) {
        AABB bounds = this.getBounds();

        for (Entity entity : this.level.getEntities(this, bounds, this::isAffected)) {
            this.ability.onHitEntity(this, owner, entity);
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

    private boolean checkSureHitEffect() {
        List<DomainExpansionEntity> domains = this.getDomainsInside();

        for (DomainExpansionEntity domain : domains) {
            if (domain.getOwner() == this.getOwner()) continue;

            int radius = this.getRadius();
            boolean isCompleted = this.getTime() >= radius * 2;

            // If the domain is open and the strength is more than or equal then break
            // else if the strength is more than or equal cancel sure hit
            if (domain.getStrength() > this.getStrength()) {
                if (isCompleted) {
                    this.discard();
                }
                return false;
            } else if (domain.getStrength() == this.getStrength()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if (!this.level.isClientSide) {
            if (!this.blocks.isEmpty()) {
                this.destroyBarrier();
            }
        }
        super.remove(pReason);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (!this.level.isClientSide) {
            //this.destroyBarrier();

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
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            if (!this.level.isClientSide) {
                int radius = this.getRadius();
                boolean isCompleted = this.getTime() >= radius * 2;

                if (this.checkSureHitEffect()) {
                    if (!this.warned || this.getTime() % 5 == 0) {
                        this.warn();
                    }
                    if (isCompleted) {
                        this.doSureHitEffect(owner);
                    }
                }

                if (this.blocks.isEmpty()) {
                    this.createBarrier(owner);
                } else if (isCompleted && !this.isInsideBarrier(owner)) {
                    this.discard();
                }
            }
        }
    }
}
