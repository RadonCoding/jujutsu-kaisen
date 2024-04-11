package radon.jujutsu_kaisen.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ImbuementHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.cursed_tool.MimicryKatanaItem;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class MimicryKatanaEntity extends Entity {
    private static final EntityDataAccessor<Variant> DATA_VARIANT = SynchedEntityData.defineId(MimicryKatanaEntity.class, EntityDataSerializer.simpleEnum(Variant.class));
    private static final EntityDataAccessor<String> DATA_TECHNIQUE = SynchedEntityData.defineId(MimicryKatanaEntity.class, EntityDataSerializers.STRING);

    @Nullable
    private UUID domainUUID;
    @Nullable
    private DomainExpansionEntity cachedDomain;

    public MimicryKatanaEntity(EntityType<? extends Entity> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public MimicryKatanaEntity(DomainExpansionEntity domain, ICursedTechnique technique, Vec3 pos) {
        super(JJKEntities.MIMICRY_KATANA.get(), domain.level());

        this.setDomain(domain);
        this.setVariant(HelperMethods.randomEnum(Variant.class));
        this.setTechnique(technique);

        this.moveTo(pos.x, pos.y, pos.z, (HelperMethods.RANDOM.nextFloat() - 0.5F) * 360.0F, (HelperMethods.RANDOM.nextFloat() - 0.5F) * 30.0F);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_VARIANT, Variant.BLACK);
        this.entityData.define(DATA_TECHNIQUE, "");
    }

    @Override
    public void tick() {
        DomainExpansionEntity domain = this.getDomain();

        if (!this.level().isClientSide && (domain == null || domain.isRemoved() || !domain.isAlive())) {
            this.discard();
        } else {
            super.tick();
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean skipAttackInteraction(@NotNull Entity pEntity) {
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        DomainExpansionEntity domain = this.getDomain();

        if (domain != null && pPlayer == domain.getOwner()) {
            ItemStack stack = new ItemStack(this::getItem);
            MimicryKatanaItem.setTechnique(stack, this.getTechnique());

            if (pPlayer.getItemInHand(pHand).isEmpty()) {
                pPlayer.setItemInHand(pHand, stack);
                this.discard();

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.interact(pPlayer, pHand);
    }

    public Item getItem() {
        return this.getVariant() == Variant.BLACK ? JJKItems.MIMICRY_KATANA_BLACK.get() : JJKItems.MIMICRY_KATANA_WHITE.get();
    }

    private Variant getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    private void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    public ICursedTechnique getTechnique() {
        return JJKCursedTechniques.getValue(new ResourceLocation(this.entityData.get(DATA_TECHNIQUE)));
    }

    private void setTechnique(ICursedTechnique technique) {
        this.entityData.set(DATA_TECHNIQUE, JJKCursedTechniques.getKey(technique).toString());
    }

    public void setDomain(@Nullable DomainExpansionEntity domain) {
        if (domain != null) {
            this.domainUUID = domain.getUUID();
            this.cachedDomain = domain;
        }
    }

    @Nullable
    public DomainExpansionEntity getDomain() {
        if (this.cachedDomain != null && !this.cachedDomain.isRemoved()) {
            return this.cachedDomain;
        } else if (this.domainUUID != null && this.level() instanceof ServerLevel) {
            this.cachedDomain = (DomainExpansionEntity) ((ServerLevel) this.level()).getEntity(this.domainUUID);
            return this.cachedDomain;
        } else {
            return null;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.domainUUID != null) {
            pCompound.putUUID("domain", this.domainUUID);
        }
        pCompound.putInt("variant", this.getVariant().ordinal());
        pCompound.putString("technique", JJKCursedTechniques.getKey(this.getTechnique()).toString());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("domain")) {
            this.domainUUID = pCompound.getUUID("domain");
        }
        this.setVariant(Variant.values()[pCompound.getInt("variant")]);
        this.setTechnique(JJKCursedTechniques.getValue(new ResourceLocation(pCompound.getString("technique"))));
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        DomainExpansionEntity entity = this.getDomain();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        DomainExpansionEntity domain = (DomainExpansionEntity) this.level().getEntity(pPacket.getData());

        if (domain != null) {
            this.setDomain(domain);
        }
    }

    public enum Variant {
        BLACK,
        WHITE
    }
}
