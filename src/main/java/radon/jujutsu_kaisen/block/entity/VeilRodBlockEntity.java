package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.item.veil.modifier.ColorModifier;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;
import radon.jujutsu_kaisen.item.veil.modifier.PlayerModifier;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VeilRodBlockEntity extends BlockEntity {
    public static final int RANGE = 128;
    public static final int INTERVAL = 10;
    private static final float COST = 0.01F;

    @Nullable
    public UUID ownerUUID;

    private boolean active;
    private int size;

    private int counter;

    private boolean first;

    public List<Modifier> modifiers;

    public VeilRodBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.VEIL_ROD.get(), pPos, pBlockState);

        this.size = ConfigHolder.SERVER.minimumVeilSize.get();
        this.modifiers = new ArrayList<>();
    }

    public boolean isValid() {
        if (!this.active) return false;
        if (!(this.level instanceof ServerLevel serverLevel)) return false;
        if (this.ownerUUID == null) return false;

        if (!(serverLevel.getEntity(this.ownerUUID) instanceof LivingEntity owner))
            return false;

        if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return false;

            ISorcererData data = cap.getSorcererData();
            float cost = COST * ((float) this.getSize() / ConfigHolder.SERVER.maximumVeilSize.get()) * (data.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);;
            return data.getEnergy() >= cost;
        }
        return true;
    }

    public boolean isAllowed(Entity entity) {
        if (entity.getUUID() == this.ownerUUID) return true;

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap != null) {
            ISorcererData data = cap.getSorcererData();

            if (data.hasTrait(Trait.HEAVENLY_RESTRICTION)) return true;
        }

        if (this.modifiers == null) return false;

        if (entity instanceof Player player) {
            for (Modifier modifier : this.modifiers) {
                if (modifier.getAction() != Modifier.Action.ALLOW || modifier.getType() != Modifier.Type.PLAYER)
                    continue;

                Component name = player.getDisplayName();

                if (name == null) continue;

                if (((PlayerModifier) modifier).getName().equals(name.getString())) {
                    return true;
                }
            }
        }

        if (cap != null) {
            ISorcererData data = cap.getSorcererData();

            for (Modifier modifier : this.modifiers) {
                if (modifier.getAction() == Modifier.Action.ALLOW) {
                    if (modifier.getType() == Modifier.Type.CURSE && data.getType() == JujutsuType.CURSE) return true;
                    if (modifier.getType() == Modifier.Type.SORCERER && data.getType() == JujutsuType.SORCERER) return true;
                } else if (modifier.getAction() == Modifier.Action.DENY) {
                    if (modifier.getType() == Modifier.Type.CURSE && data.getType() == JujutsuType.CURSE) return false;
                    if (modifier.getType() == Modifier.Type.SORCERER && data.getType() == JujutsuType.SORCERER) return false;
                }
            }
        }
        return false;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VeilRodBlockEntity pBlockEntity) {
        if (!pBlockEntity.isValid()) return;

        VeilHandler.veil(pLevel.dimension(), pPos);

        pBlockEntity.counter++;

        if (!pBlockEntity.first) {
            if (pBlockEntity.counter != INTERVAL) return;

            pBlockEntity.counter = 0;
        }

        if (pBlockEntity.ownerUUID == null || !(((ServerLevel) pLevel).getEntity(pBlockEntity.ownerUUID) instanceof LivingEntity owner))
            return;

        BlockState replacement = JJKBlocks.VEIL.get().defaultBlockState();

        for (Modifier modifier : pBlockEntity.modifiers) {
            if (modifier.getType() == Modifier.Type.COLOR) {
                replacement = replacement.setValue(VeilBlock.COLOR, ((ColorModifier) modifier).getColor());
            } else if (modifier.getType() == Modifier.Type.TRANSPARENT) {
                replacement = replacement.setValue(VeilBlock.TRANSPARENT, true);
            }
        }

        int radius = pBlockEntity.size;

        for (int y = radius; y >= -radius; y--) {
            int delay = Math.abs(y - radius);

            if (pBlockEntity.counter < delay) break;

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius && distance >= radius - 1) {
                        BlockPos pos = pPos.offset(x, y, z);

                        if (!pLevel.isInWorldBounds(pos)) continue;

                        BlockState state = pLevel.getBlockState(pos);

                        if (!state.is(BlockTags.DOORS) && !state.is(BlockTags.TRAPDOORS) && !state.getCollisionShape(pLevel, pos).isEmpty()) continue;

                        if (pos.getY() < pLevel.getMinBuildHeight()) continue;

                        if (!VeilHandler.isProtectedBy(((ServerLevel) pLevel), pLevel.dimension(), pPos, pos)) continue;

                        BlockEntity existing = pLevel.getBlockEntity(pos);

                        CompoundTag saved = null;

                        if (existing instanceof VeilBlockEntity be) {
                            state = be.getOriginal();
                        } else if (existing != null) {
                            saved = existing.saveWithFullMetadata();
                        }

                        if (!(existing instanceof VeilBlockEntity)) {
                            // Creating a new veil block consumes cursed energy from the caster
                            if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
                                IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                                if (cap == null) continue;

                                ISorcererData data = cap.getSorcererData();

                                float cost = COST * (data.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);

                                if (data.getEnergy() < cost) continue;

                                data.useEnergy(cost);

                                if (owner instanceof ServerPlayer player) {
                                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
                                }
                            }

                            pLevel.setBlock(pos, replacement,
                                    Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                        }

                        if (pLevel.getBlockEntity(pos) instanceof VeilBlockEntity be) {
                            be.create(pPos, (radius * 2) - delay, radius, state, saved);
                        }
                    }
                }
            }
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.first = true;
        this.setChanged();
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

    private void markUpdated() {
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putBoolean("first", this.first);

        if (this.ownerUUID != null) {
            pTag.putUUID("owner", this.ownerUUID);
        }
        pTag.putBoolean("active", this.active);
        pTag.putInt("size", this.size);

        pTag.putInt("counter", this.counter);

        if (this.modifiers != null) {
            pTag.put("modifiers", ModifierUtils.serialize(this.modifiers));
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.first = pTag.getBoolean("first");

        if (pTag.contains("owner")) {
            this.ownerUUID = pTag.getUUID("owner");
        }
        this.active = pTag.getBoolean("active");
        this.size = pTag.getInt("size");

        this.counter = pTag.getInt("counter");

        if (pTag.contains("modifiers")) {
            this.modifiers = ModifierUtils.deserialize(pTag.getList("modifiers", Tag.TAG_COMPOUND));
        }

        if (this.level != null) {
            this.markUpdated();
        }
    }
}
