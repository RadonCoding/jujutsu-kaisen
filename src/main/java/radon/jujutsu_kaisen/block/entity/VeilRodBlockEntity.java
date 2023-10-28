package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.veil.modifier.ColorModifier;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class VeilRodBlockEntity extends BlockEntity {
    public static final int RANGE = 128;
    public static final int INTERVAL = 10;
    private static final float COST = 0.5F;

    private int counter;
    private int size;

    @Nullable
    public List<Modifier> modifiers;
    @Nullable
    public UUID ownerUUID;

    public VeilRodBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.VEIL_ROD.get(), pPos, pBlockState);

        this.size = ConfigHolder.SERVER.minimumVeilSize.get();
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VeilRodBlockEntity pBlockEntity) {
        VeilHandler.create(pLevel.dimension(), pPos);

        if (++pBlockEntity.counter != INTERVAL) return;

        pBlockEntity.counter = 0;

        if (pBlockEntity.ownerUUID == null) return;

        if (!(((ServerLevel) pLevel).getEntity(pBlockEntity.ownerUUID) instanceof LivingEntity owner) || !owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

        if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            float cost = COST * ((float) pBlockEntity.getSize() / ConfigHolder.SERVER.maximumVeilSize.get());

            if (cap.getEnergy() < cost) return;

            cap.useEnergy(cost);

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            }
        }

        BlockState replacement = JJKBlocks.VEIL.get().defaultBlockState();

        if (pBlockEntity.modifiers != null) {
            for (Modifier modifier : pBlockEntity.modifiers) {
                if (modifier.getType() == Modifier.Type.COLOR) {
                    replacement = replacement.setValue(VeilBlock.COLOR, ((ColorModifier) modifier).getColor());
                } else if (modifier.getType() == Modifier.Type.TRANSPARENT) {
                    replacement = replacement.setValue(VeilBlock.TRANSPARENT, true);
                }
            }
        }

        AABB bounds = AABB.ofSize(pPos.getCenter(), pBlockEntity.size, pBlockEntity.size, pBlockEntity.size);

        List<DomainExpansionEntity> domains = HelperMethods.getEntityCollisionsOfClass(DomainExpansionEntity.class, pLevel, bounds);

        for (int x = -pBlockEntity.size; x <= pBlockEntity.size; x++) {
            for (int y = -pBlockEntity.size; y <= pBlockEntity.size; y++) {
                for (int z = -pBlockEntity.size; z <= pBlockEntity.size; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < pBlockEntity.size && distance >= pBlockEntity.size - 1) {
                        BlockPos pos = pPos.offset(x, y, z);

                        boolean blocked = false;

                        for (DomainExpansionEntity domain : domains) {
                            LivingEntity opponent = domain.getOwner();

                            if (opponent == null) continue;

                            ISorcererData veilCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                            ISorcererData domainCap = opponent.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                            if (domainCap.getAbilityPower(opponent) < veilCap.getAbilityPower(owner)) continue;

                            if (domain.isInsideBarrier(pos)) {
                                if (pLevel.getBlockEntity(pos) instanceof VeilBlockEntity be) {
                                    be.destroy();
                                }
                                blocked = true;
                            }
                        }

                        if (blocked) continue;

                        BlockEntity existing = pLevel.getBlockEntity(pos);

                        BlockState state;

                        if (existing instanceof VeilBlockEntity be) {
                            state = be.getOriginal();
                        } else if (existing != null) {
                            continue;
                        } else {
                            state = pLevel.getBlockState(pos);
                        }

                        pLevel.setBlock(pos, replacement,
                                Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);

                        if (pLevel.getBlockEntity(pos) instanceof VeilBlockEntity be) {
                            be.create(pPos, pBlockEntity.size, state);
                        }
                    }
                }
            }
        }
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
        this.setChanged();
    }

    public void setOwner(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        this.setChanged();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        if (this.ownerUUID != null) {
            pTag.putUUID("owner", this.ownerUUID);
        }
        pTag.putInt("counter", this.counter);
        pTag.putInt("size", this.size);

        if (this.modifiers != null) {
            pTag.put("modifiers", ModifierUtils.serialize(this.modifiers));
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        if (pTag.contains("owner")) {
            this.ownerUUID = pTag.getUUID("owner");
        }
        this.counter = pTag.getInt("counter");
        this.size = pTag.getInt("size");

        if (pTag.contains("modifiers")) {
            this.modifiers = ModifierUtils.deserialize(pTag.getList("modifiers", Tag.TAG_COMPOUND));
        }
    }
}
