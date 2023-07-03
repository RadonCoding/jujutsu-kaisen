package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.block.DomainBlock;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.SyncSorcererDataS2CPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClosedDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(ClosedDomainExpansionEntity.class, EntityDataSerializers.INT);
    private static final float STRENGTH = 100.0F;

    private final Map<BlockPos, BlockState> blocks = new HashMap<>();

    private DomainExpansion ability;

    private BlockState block;
    private int duration;


    public ClosedDomainExpansionEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public AABB getBounds() {
        int radius = this.getRadius();
        return new AABB(this.getX() - radius, this.getY() - (double) (radius / 2), this.getZ() - radius,
                this.getX() + radius, this.getY() + (double) (radius / 2), this.getZ() + radius);
    }

    public ClosedDomainExpansionEntity(LivingEntity owner, DomainExpansion ability, float strength, BlockState block, int radius, int duration) {
        super(JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), owner, strength);

        this.moveTo(owner.getX(), owner.getY(), owner.getZ());

        this.ability = ability;

        this.block = block;
        this.duration = duration;

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
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);

        this.destroyBarrier();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_RADIUS, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.ability = (DomainExpansion) JJKAbilities.getValue(new ResourceLocation(pCompound.getString("ability")));
        this.block = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), pCompound.getCompound("block"));
        this.duration = pCompound.getInt("duration");

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

        pCompound.putString("ability", JJKAbilities.getKey(this.ability).toString());
        pCompound.put("block", NbtUtils.writeBlockState(this.block));
        pCompound.putInt("duration", this.duration);
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

    @Override
    public boolean isPickable() {
        return false;
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

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius - 1) {
                        BlockPos pos = center.offset(x, y, z);

                        if (entity.blockPosition().equals(pos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
                        this.blocks.put(pos, state);

                        int delay = radius - y;

                        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                            cap.delayTickEvent((ownerClone) -> {
                                if (!this.isDeadOrDying()) {
                                    ownerClone.level.setBlockAndUpdate(pos, this.block);
                                }
                            }, delay);
                        });
                    }
                }
            }
        }
    }

    public void destroyBarrier() {
        for (Map.Entry<BlockPos, BlockState> entry : this.blocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            if (state.getBlock() instanceof DomainBlock) continue;

            if (state.isAir()) {
                this.level.destroyBlock(pos, false);
            } else {
                this.level.setBlockAndUpdate(pos, state);
            }
        }

        Entity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.setBurnout(DomainExpansion.BURNOUT);

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            });
        }
        this.discard();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        int radius = this.getRadius() * 2;
        return EntityDimensions.fixed(radius, radius);
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

        for (Entity entity : this.level.getEntities(this, bounds)) {
            if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;

            if (!this.isInsideBarrier(entity)) continue;

            AtomicBoolean result = new AtomicBoolean();

            entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.getTrait() == Trait.HEAVENLY_RESTRICTION) {
                    result.set(true);
                }
            });

            if (!result.get()) {
                entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.onInsideDomain(this));
                this.ability.onHitEntity(this, owner, entity);
            }
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

    private boolean checkSureHitEffect(Entity owner) {
        AtomicBoolean result = new AtomicBoolean(true);

        List<DomainExpansionEntity> domains = this.getDomainsInside();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (DomainExpansionEntity domain : domains) {
                if (domain.getOwner() == this.getOwner()) continue;

                if (domain instanceof OpenDomainExpansionEntity) {
                    this.destroyBarrier();
                }

                if (domain.getStrength() >= this.getStrength()) {
                    result.set(false);
                }
            }
        });
        return result.get();
    }

    @Override
    protected void doPush(@NotNull Entity p_20971_) {

    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();

        if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            super.tick();

            this.refreshDimensions();

            if (!this.level.isClientSide && entity instanceof LivingEntity owner) {
                int radius = this.getRadius();
                boolean isCompleted = this.getTime() >= radius * 2;

                if (isCompleted && this.checkSureHitEffect(owner)) {
                    this.doSureHitEffect(owner);
                }

                if (this.blocks.isEmpty()) {
                    this.createBarrier(owner);
                }
                else if (this.duration-- == 0 || (isCompleted && !this.isInsideBarrier(owner))) {
                    this.destroyBarrier();
                }
            }
        } else {
            this.discard();
        }
    }
}
