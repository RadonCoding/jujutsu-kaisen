package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class ForestWaveEntity extends Entity {
    private static final float DAMAGE = 10.0F;

    private BlockState state;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    private boolean damage;

    public ForestWaveEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ForestWaveEntity(LivingEntity owner) {
        super(JJKEntities.FOREST_WAVE.get(), owner.level);

        this.setOwner(owner);

        this.state = Blocks.OAK_WOOD.defaultBlockState();
        this.blocksBuilding = true;
    }

    public void setDamage(boolean damage) {
        this.damage = damage;
    }

    public BlockState getState() {
        return this.state;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (!this.damage) return;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (Entity entity : HelperMethods.getEntityCollisions(this.level, this.getBoundingBox())) {
                if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner || entity instanceof ForestWaveEntity) continue;

                entity.setDeltaMovement(this.position().subtract(entity.position()).normalize().reverse());
                entity.hurtMarked = true;

                entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.FOREST_WAVE.get()), DAMAGE * cap.getGrade().getRealPower(owner));
            }
        });
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = this.getOwner();

        if (!this.level.isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive() || !JJKAbilities.isChanneling(owner, JJKAbilities.FOREST_WAVE.get()))) {
            this.discard();
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
        } else if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        pCompound.putBoolean("damage", this.damage);
        pCompound.put("state", NbtUtils.writeBlockState(this.state));

        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        this.damage = pCompound.getBoolean("damage");
        this.state = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), pCompound.getCompound("state"));

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
    }

    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.getState()));
    }

    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        this.state = Block.stateById(pPacket.getData());
        this.blocksBuilding = true;
        double d0 = pPacket.getX();
        double d1 = pPacket.getY();
        double d2 = pPacket.getZ();
        this.setPos(d0, d1, d2);
    }
}
