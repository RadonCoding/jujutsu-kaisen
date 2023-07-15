package radon.jujutsu_kaisen.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.SyncOverlayDataRemoteS2CPacket;

import java.util.*;

public class OverlayData implements IOverlayData {
    private final Set<ResourceLocation> local;
    private final Map<UUID, Set<ResourceLocation>> remote;

    private static final ResourceLocation SIX_EYES = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/six_eyes.png");

    public OverlayData() {
        this.local = new LinkedHashSet<>();
        this.remote = new HashMap<>();
    }

    @Override
    public void sync(UUID identifier) {
        PacketHandler.broadcast(new SyncOverlayDataRemoteS2CPacket(identifier, this.serializeNBT()));
    }

    @Override
    public void addLocalOverlay(LivingEntity owner, ResourceLocation overlay) {
        if (this.local.add(overlay) && !owner.level.isClientSide) {
            this.sync(owner.getUUID());
        }
    }

    @Override
    public void removeLocalOverlay(LivingEntity owner, ResourceLocation overlay) {
        if (this.local.remove(overlay) && !owner.level.isClientSide) {
            this.sync(owner.getUUID());
        }
    }

    @Override
    public Set<ResourceLocation> getLocalOverlays() {
        return this.local;
    }

    @Override
    public Set<ResourceLocation> getRemoteOverlays(UUID identifier) {
        return this.remote.computeIfAbsent(identifier, x -> new LinkedHashSet<>());
    }

    @Override
    public boolean isSynced(UUID identifier) {
        return this.remote.containsKey(identifier);
    }

    @Override
    public void tick(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasTrait(Trait.SIX_EYES)) {
                this.addLocalOverlay(owner, SIX_EYES);
            } else if (this.local.contains(SIX_EYES)) {
                this.removeLocalOverlay(owner, SIX_EYES);
            }
        });
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag overlaysTag = new ListTag();

        for (ResourceLocation overlay : this.local) {
            overlaysTag.add(StringTag.valueOf(overlay.toString()));
        }
        nbt.put("overlays", overlaysTag);
        return nbt;
    }

    @Override
    public void deserializeLocalNBT(CompoundTag nbt) {
        for (Tag tag : nbt.getList("overlays", Tag.TAG_STRING)) {
            this.local.add(new ResourceLocation(tag.getAsString()));
        }
    }

    @Override
    public void deserializeRemoteNBT(UUID identifier, CompoundTag nbt) {
        Set<ResourceLocation> remote = new LinkedHashSet<>();

        for (Tag tag : nbt.getList("overlays", Tag.TAG_STRING)) {
            remote.add(new ResourceLocation(tag.getAsString()));
        }
        this.remote.put(identifier, remote);
    }
}