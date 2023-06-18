package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import radon.jujutsu_kaisen.capability.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.SpecialTrait;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class DomainExpansionEntity extends Mob {
    private static final float STRENGTH = 100.0F;

    private final Map<BlockPos, BlockState> blocks = new HashMap<>();

    private DomainExpansion ability;

    private BlockState block;
    private int radius;
    private int duration;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;

    public DomainExpansionEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DomainExpansionEntity(LivingEntity owner, DomainExpansion ability, BlockState block, int radius, int duration) {
        super(JJKEntities.DOMAIN_EXPANSION.get(), owner.level);

        this.moveTo(owner.getX(), owner.getY(), owner.getZ());

        this.setOwner(owner);

        this.ability = ability;

        this.block = block;
        this.radius = radius;
        this.duration = duration;

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);

            if (attribute != null) {
                attribute.setBaseValue(STRENGTH * cap.getGrade().getPower());
            }
        });
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.refreshDimensions();
    }

    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.ability = (DomainExpansion) JJKAbilities.getValue(new ResourceLocation(pCompound.getString("ability")));
        this.block = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), pCompound.getCompound("block"));
        this.radius = pCompound.getInt("radius");
        this.duration = pCompound.getInt("duration");

        for (Tag key : pCompound.getList("blocks", Tag.TAG_COMPOUND)) {
            CompoundTag block = (CompoundTag) key;
            this.blocks.put(NbtUtils.readBlockPos(block.getCompound("pos")),
                    NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), block.getCompound("state")));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putString("ability", JJKAbilities.getKey(this.ability).toString());
        pCompound.put("block", NbtUtils.writeBlockState(this.block));
        pCompound.putInt("radius", this.radius);
        pCompound.putInt("duration", this.duration);

        ListTag blocksTag = new ListTag();

        for (Map.Entry<BlockPos, BlockState> entry : this.blocks.entrySet()) {
            CompoundTag block = new CompoundTag();
            block.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
            block.put("state", NbtUtils.writeBlockState(entry.getValue()));
            blocksTag.add(block);
        }
        pCompound.put("blocks", blocksTag);
    }

    private boolean isInsideBarrier(Entity entity) {
        BlockPos center = this.blockPosition().offset(0, this.radius / 2, 0);

        for (int x = -this.radius; x <= this.radius; x++) {
            for (int y = -this.radius; y <= this.radius; y++) {
                for (int z = -this.radius; z <= this.radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < this.radius) {
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
        BlockPos center = this.blockPosition().offset(0, this.radius / 2, 0);

        for (int x = -this.radius; x <= this.radius; x++) {
            for (int y = -this.radius; y <= this.radius; y++) {
                for (int z = -this.radius; z <= this.radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < this.radius && distance >= this.radius - 1) {
                        BlockPos pos = center.offset(x, y, z);
                        BlockState state = this.level.getBlockState(pos);
                        this.blocks.put(pos, state);

                        int delay = this.radius - y;

                        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                            cap.delayTickEvent((ownerClone) -> {
                                ownerClone.level.setBlockAndUpdate(pos, this.block);
                            }, delay);
                        });
                    }
                }
            }
        }
    }

    private void destroyBarrier() {
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
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.setBurnout(DomainExpansion.BURNOUT));
        }
        this.discard();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        Entity entity = pSource.getDirectEntity();

        if (entity == null) return false;

        if (this.isInsideBarrier(pSource.getDirectEntity())) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();

        if (this.level.isClientSide || (owner == null || !owner.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            super.tick();

            if (owner != null) {
                boolean isCompleted = this.tickCount >= this.radius * 2;

                if (isCompleted) {
                    AABB bounds = new AABB(this.getX() - this.radius, this.getY(), this.getZ() - this.radius,
                            this.getX() + this.radius, this.getY() + this.radius, this.getZ() + this.radius);

                    for (Entity entity : this.level.getEntities(owner, bounds)) {
                        AtomicBoolean result = new AtomicBoolean();

                        entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                            if (cap.getTrait() == SpecialTrait.HEAVENLY_RESTRICTION) {
                                result.set(true);
                            }
                        });

                        if (!result.get() && this.isInsideBarrier(entity)) {
                            this.ability.onHit(entity);
                        }
                    }
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

    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity owner = this.getOwner();
        return new ClientboundAddEntityPacket(this, owner == null ? 0 : owner.getId());
    }

    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        Entity owner = this.level.getEntity(pPacket.getData());

        if (owner != null) {
            this.setOwner(owner);
        }
    }
}
