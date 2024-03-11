package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.JJKEntities;

import javax.annotation.Nullable;
import java.util.UUID;

public class WoodShieldEntity extends Mob {
    private static final float STRENGTH = 50.0F;

    @Nullable
    private Vec3 pos;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public WoodShieldEntity(EntityType<? extends Mob> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public WoodShieldEntity(LivingEntity owner) {
        super(JJKEntities.WOOD_SHIELD.get(), owner.level());

        this.setOwner(owner);
        this.pos = owner.position();

        this.setPos(owner.position());

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);

        if (attribute != null) {
            attribute.setBaseValue(STRENGTH * data.getAbilityOutput(JJKAbilities.WOOD_SHIELD.get()));
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.level().addFreshEntity(new WoodShieldSegmentEntity(this));
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        LivingEntity owner = this.getOwner();

        if (owner == null) {
            return super.getDimensions(pPose);
        }
        return EntityDimensions.fixed(owner.getBbWidth(), owner.getBbHeight()).scale(1.25F);
    }

    @Override
    public void tick() {
        this.refreshDimensions();

        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && owner != null) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.WOOD_SHIELD.get())) {
                this.discard();
                return;
            }
        }

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();

            if (owner != null && this.pos != null) {
                owner.teleportTo(this.pos.x, this.pos.y, this.pos.z);
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void aiStep() {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
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
    public boolean isInWall() {
        return false;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.pos != null) {
            pCompound.putDouble("pos_x", this.pos.x);
            pCompound.putDouble("pos_y", this.pos.y);
            pCompound.putDouble("pos_z", this.pos.z);
        }

        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.pos = new Vec3(pCompound.getDouble("pos_x"), pCompound.getDouble("pos_y"), pCompound.getDouble("pos_z"));

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
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
