package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

public class DomainSkyBlockEntity extends DomainBlockEntity {
    @Nullable
    private ResourceLocation domain;

    public DomainSkyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DOMAIN_SKY.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DomainSkyBlockEntity pBlockEntity) {
        DomainBlockEntity.tick(pLevel, pPos, pState, pBlockEntity);

        if (pBlockEntity.domain == null) {
            if (!(((ServerLevel) pLevel).getEntity(pBlockEntity.identifier) instanceof DomainExpansionEntity domain))
                return;

            pBlockEntity.setDomain(JJKAbilities.getKey(domain.getAbility()));
        }
    }

    public void setDomain(@Nullable ResourceLocation domain) {
        this.domain = domain;
        this.setChanged();
    }

    public @Nullable ResourceLocation getDomain() {
        return this.domain;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return this.saveWithoutMetadata(pRegistries);
    }

    private void markUpdated() {
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        if (this.domain != null) {
            pTag.putString("domain", this.domain.toString());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        if (pTag.contains("domain")) {
            this.domain = new ResourceLocation(pTag.getString("domain"));
        }

        if (this.level != null) {
            this.markUpdated();
        }
    }
}
