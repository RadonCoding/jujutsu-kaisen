package radon.jujutsu_kaisen.data.domain;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.packet.s2c.SyncDomainDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncVisualDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.UpdateDomainInfoS2CPacket;

import java.util.*;

public class DomainData implements IDomainData {
    @Nullable
    private ResourceKey<Level> original;

    private final Set<DomainInfo> domains;

    private final Level level;

    public DomainData(Level level) {
        this.level = level;

        this.domains = new LinkedHashSet<>();
    }

    @Override
    public @Nullable ResourceKey<Level> getOriginal() {
        return this.original;
    }

    @Override
    public void init(ResourceKey<Level> original) {
        this.original = original;
    }

    @Override
    public boolean hasDomain(UUID owner) {
        for (DomainInfo info : this.domains) {
            if (info.owner().equals(owner)) return true;
        }
        return false;
    }

    // TODO: Implement removal of domains
    @Override
    public void update(DomainExpansionEntity domain) {
        LivingEntity owner = domain.getOwner();

        if (owner == null) return;

        DomainInfo info = new DomainInfo(owner.getUUID(), domain.getUUID(), domain.getAbility(), domain.getStrength());
        this.domains.add(info);

        PacketDistributor.sendToAllPlayers(new UpdateDomainInfoS2CPacket(this.level.dimension(), info));
    }

    @Override
    public void update(DomainInfo info) {
        this.domains.add(info);
    }

    @Override
    public Set<DomainInfo> getDomains() {
        return this.domains;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();

        if (this.original != null) {
            nbt.putString("original", this.original.location().toString());
        }

        ListTag domainsTag = new ListTag();

        for (DomainInfo info : this.domains) {
            domainsTag.add(DomainInfo.CODEC.encode(info, provider.createSerializationContext(NbtOps.INSTANCE),
                    new CompoundTag()).getOrThrow());
        }
        nbt.put("domains", domainsTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains("original")) {
            this.original = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("original")));
        }

        this.domains.clear();

        for (Tag tag : nbt.getList("domains", Tag.TAG_COMPOUND)) {
            this.domains.add(DomainInfo.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow());
        }
    }
}
