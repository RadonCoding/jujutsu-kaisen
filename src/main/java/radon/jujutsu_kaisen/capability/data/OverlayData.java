package radon.jujutsu_kaisen.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.layer.overlay.JJKOverlays;
import radon.jujutsu_kaisen.client.layer.overlay.Overlay;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncOverlayDataRemoteS2CPacket;

import java.util.*;

public class OverlayData implements IOverlayData {
    private final Set<Overlay> local;
    private final Map<UUID, Set<Overlay>> remote;

    public OverlayData() {
        this.local = new LinkedHashSet<>();
        this.remote = new HashMap<>();
    }

    @Override
    public void sync(LivingEntity owner) {
        PacketHandler.broadcastNearby(new SyncOverlayDataRemoteS2CPacket(owner.getUUID(), this.serializeNBT()), owner);
    }

    @Override
    public void addLocalOverlay(LivingEntity owner, Overlay overlay) {
        if (this.local.add(overlay) && !owner.level.isClientSide) {
            this.sync(owner);
        }
    }

    @Override
    public void removeLocalOverlay(LivingEntity owner, Overlay overlay) {
        if (this.local.remove(overlay) && !owner.level.isClientSide) {
            this.sync(owner);
        }
    }

    @Override
    public Set<Overlay> getLocalOverlays() {
        return this.local;
    }

    @Override
    public Set<Overlay> getRemoteOverlays(UUID identifier) {
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
                this.addLocalOverlay(owner, JJKOverlays.SIX_EYES.get());
            } else if (this.local.contains(JJKOverlays.SIX_EYES.get())) {
                this.removeLocalOverlay(owner, JJKOverlays.SIX_EYES.get());
            }
        });
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag overlaysTag = new ListTag();

        for (Overlay overlay : this.local) {
            overlaysTag.add(StringTag.valueOf(JJKOverlays.getKey(overlay).toString()));
        }
        nbt.put("overlays", overlaysTag);
        return nbt;
    }

    @Override
    public void deserializeLocalNBT(CompoundTag nbt) {
        for (Tag tag : nbt.getList("overlays", Tag.TAG_STRING)) {
            this.local.add(JJKOverlays.getValue(new ResourceLocation(tag.getAsString())));
        }
    }

    @Override
    public void deserializeRemoteNBT(UUID identifier, CompoundTag nbt) {
        Set<Overlay> remote = new LinkedHashSet<>();

        for (Tag tag : nbt.getList("overlays", Tag.TAG_STRING)) {
            remote.add(JJKOverlays.getValue(new ResourceLocation(tag.getAsString())));
        }
        this.remote.put(identifier, remote);
    }
}