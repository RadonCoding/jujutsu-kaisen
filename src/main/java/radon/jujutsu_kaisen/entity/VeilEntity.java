package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.base.IBarrier;
import radon.jujutsu_kaisen.entity.base.IVeil;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.item.veil.modifier.ColorModifier;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.List;
import java.util.UUID;

public class VeilEntity extends Entity implements IVeil {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(VeilEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(VeilEntity.class, EntityDataSerializers.INT);

    private static final float COST = 0.0001F;
    private static final int CHECK_INTERVAL = 20;
    private static final int HEAL_INTERVAL = 5 * 20;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    private List<Modifier> modifiers;

    @Nullable
    private BlockPos center;
    
    private int total;

    public VeilEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public VeilEntity(LivingEntity owner, Vec3 pos, int radius, List<Modifier> modifiers) {
        super(JJKEntities.VEIL.get(), owner.level());

        this.setOwner(owner);

        this.setPos(pos.subtract(0.0D, radius, 0.0D));

        this.setRadius(radius);

        this.modifiers = modifiers;
    }

    public VeilEntity(LivingEntity owner, Vec3 pos, int radius, List<Modifier> modifiers, @Nullable BlockPos center) {
        this(owner, pos, radius, modifiers);

        this.center = center;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TIME, 0);
        this.entityData.define(DATA_RADIUS, 0);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    public int getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public void setRadius(int radius) {
        this.entityData.set(DATA_RADIUS, radius);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putInt("total", this.total);
        pCompound.putInt("time", this.getTime());
        pCompound.putInt("radius", this.getRadius());
        pCompound.put("modifiers", ModifierUtils.serialize(this.modifiers));

        if (this.center != null) {
            pCompound.put("center", NbtUtils.writeBlockPos(this.center));
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.total = pCompound.getInt("total");
        this.setTime(pCompound.getInt("time"));
        this.setRadius(pCompound.getInt("radius"));
        this.modifiers = ModifierUtils.deserialize(pCompound.getList("modifiers", CompoundTag.TAG_LIST));

        if (pCompound.contains("center")) {
            this.center = NbtUtils.readBlockPos(pCompound.getCompound("center"));
        }
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public boolean isInsideBarrier(BlockPos pos) {
        int radius = this.getRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < (radius - 2) * (radius - 2);
    }

    @Override
    public boolean isBarrier(BlockPos pos) {
        int radius = this.getRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < radius * radius;
    }

    @Override
    public AABB getBounds() {
        return this.getBoundingBox();
    }

    @Override
    public boolean hasSureHitEffect() {
        return false;
    }

    @Override
    public boolean checkSureHitEffect() {
        return false;
    }

    @Override
    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    private boolean charge(LivingEntity owner) {
        // Creating a new veil block consumes cursed energy from the caster
        if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return false;

            ISorcererData data = cap.getSorcererData();

            float cost = COST * (data.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);

            if (data.getEnergy() < cost) return false;

            data.useEnergy(cost);

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
            }
        }
        return true;
    }

    private void createBarrier() {
        this.total = 0;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        BlockState replacement = JJKBlocks.VEIL.get().defaultBlockState();

        for (Modifier modifier : this.modifiers) {
            if (modifier.getType() == Modifier.Type.COLOR) {
                replacement = replacement.setValue(VeilBlock.COLOR, ((ColorModifier) modifier).getColor());
            } else if (modifier.getType() == Modifier.Type.TRANSPARENT) {
                replacement = replacement.setValue(VeilBlock.TRANSPARENT, true);
            }
        }

        int radius = this.getRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        if (this.getTime() - 1 == 0) {
            int diameter = radius * 2;

            for (CursedSpirit curse : this.level().getEntitiesOfClass(CursedSpirit.class, AABB.ofSize(this.blockPosition().getCenter(), diameter, diameter, diameter))) {
                curse.setHiding(false);
            }
        }

        for (int y = radius; y >= -radius; y--) {
            int delay = Math.abs(y - radius);

            if (this.getTime() < delay) break;

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius && distance >= radius - 1) {
                        BlockPos pos = center.offset(x, y, z);

                        if (!this.level().isInWorldBounds(pos)) continue;

                        BlockState state = this.level().getBlockState(pos);

                        if (state.is(Blocks.BEDROCK)) continue;

                        if (!this.isOwned(pos)) continue;

                        BlockEntity existing = this.level().getBlockEntity(pos);

                        CompoundTag saved = null;

                        if (existing != null) {
                            saved = existing.saveWithFullMetadata();
                        }

                        if (!(existing instanceof VeilBlockEntity)) {
                            if (!this.charge(owner)) continue;

                            if (this.level().setBlock(pos, replacement,
                                    Block.UPDATE_CLIENTS)) this.total++;
                        } else {
                            this.total++;
                        }

                        if (this.level().getBlockEntity(pos) instanceof VeilBlockEntity be) {
                            be.create(this.getUUID(), this.ownerUUID, (radius * 2) - delay, radius, this.modifiers, state, saved);
                        }
                    }
                }
            }
        }
    }

    private void destroyBarrier() {
        this.total = 0;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        BlockState replacement = JJKBlocks.VEIL.get().defaultBlockState();

        for (Modifier modifier : this.modifiers) {
            if (modifier.getType() == Modifier.Type.COLOR) {
                replacement = replacement.setValue(VeilBlock.COLOR, ((ColorModifier) modifier).getColor());
            } else if (modifier.getType() == Modifier.Type.TRANSPARENT) {
                replacement = replacement.setValue(VeilBlock.TRANSPARENT, true);
            }
        }

        int radius = this.getRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        for (int y = radius; y >= -radius; y--) {
            int delay = Math.abs(y - radius);

            if (this.getTime() < delay) break;

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius && distance >= radius - 1) {
                        BlockPos pos = center.offset(x, y, z);

                        if (!this.level().isInWorldBounds(pos)) continue;

                        BlockState state = this.level().getBlockState(pos);

                        if (state.is(Blocks.BEDROCK)) continue;

                        if (!this.isOwned(pos)) continue;

                        if (!(this.level().getBlockEntity(pos) instanceof VeilBlockEntity be)) continue;

                        if (be.getParentUUID() == null || !be.getParentUUID().equals(this.getUUID())) continue;

                        be.destroy();
                    }
                }
            }
        }
    }

    private void check() {
        if (this.center != null) {
            if (!this.level().getBlockState(this.center).is(JJKBlocks.VEIL_ROD)) {
                this.discard();
                return;
            }
        }

        int count = 0;

        int radius = this.getRadius();

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius && distance >= radius - 1) {
                        BlockPos pos = center.offset(x, y, z);

                        if (this.level().getBlockEntity(pos) instanceof VeilBlockEntity) count++;
                    }
                }
            }
        }

        if ((float) count / this.total < 0.75F) {
            this.discard();
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (!this.level().isClientSide) {
            VeilHandler.barrier(this.level().dimension(), this.getUUID());
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        this.destroyBarrier();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        int radius = this.getRadius() * 2;
        return EntityDimensions.fixed(radius, radius);
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        this.setTime(this.getTime() + 1);

        if (this.level().isClientSide) return;

        int radius = this.getRadius();
        boolean completed = this.getTime() >= radius * 2;

        if (completed) {
            if (this.getTime() % CHECK_INTERVAL == 0) {
                this.check();
            }

            if (this.getTime() % HEAL_INTERVAL == 0) {
                this.createBarrier();
            }
        } else {
            this.createBarrier();
        }
    }
}
