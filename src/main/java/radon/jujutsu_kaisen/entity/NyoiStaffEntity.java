package radon.jujutsu_kaisen.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

import java.util.UUID;

public class NyoiStaffEntity extends Entity {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(NyoiStaffEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> DATA_CHARGED = SynchedEntityData.defineId(NyoiStaffEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public NyoiStaffEntity(EntityType<? extends Entity> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public NyoiStaffEntity(LivingEntity owner, ItemStack stack, Vec3 pos) {
        super(JJKEntities.NYOI_STAFF.get(), owner.level());

        this.setOwner(owner);
        this.setItem(stack);

        this.setPos(pos);
    }

    public boolean isCharged() {
        return this.entityData.get(DATA_CHARGED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(DATA_CHARGED, charged);
    }

    private void setItem(ItemStack stack) {
        this.entityData.set(DATA_ITEM_STACK, stack.copyWithCount(1));
    }

    public ItemStack getItem() {
        return this.entityData.get(DATA_ITEM_STACK);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (pPlayer == this.getOwner()) {
            if (pPlayer.getItemInHand(pHand).isEmpty()) {
                pPlayer.setItemInHand(pHand, this.getItem());
                this.discard();

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.interact(pPlayer, pHand);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isCharged()) return;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        for (int i = 0; i < 8; i++) {
            double x = this.getX() + (this.random.nextDouble() - 0.5D) * (this.getBbWidth() * 1.25F);
            double y = this.getY() + this.random.nextDouble() * (this.getBbHeight());
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * (this.getBbWidth() * 1.25F);
            double speed = (this.getBbHeight() * 0.1F) * this.random.nextDouble();
            this.level().addParticle(new CursedEnergyParticle.Options(ParticleColors.getCursedEnergyColor(owner), this.getBbWidth() * 0.5F,
                    0.2F, 16), x, y, z, 0.0D, speed, 0.0D);
        }

        for (int i = 0; i < 4; i++) {
            double x = this.getX() + (this.random.nextDouble() - 0.5D) * (this.getBbWidth() * 2);
            double y = this.getY() + this.random.nextDouble() * (this.getBbHeight() * 1.25F);
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * (this.getBbWidth() * 2);
            this.level().addParticle(new LightningParticle.Options(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1),
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        pBuilder.define(DATA_ITEM_STACK, ItemStack.EMPTY);
        pBuilder.define(DATA_CHARGED, false);
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
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.put("item", this.getItem().save(this.registryAccess()));
        pCompound.putBoolean("charged", this.isCharged());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.setItem(ItemStack.parse(this.registryAccess(), pCompound.getCompound("item")).orElseThrow());
        this.setCharged(pCompound.getBoolean("charged"));
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        LivingEntity owner = (LivingEntity) this.level().getEntity(pPacket.getData());

        if (owner != null) {
            this.setOwner(owner);
        }
    }
}
