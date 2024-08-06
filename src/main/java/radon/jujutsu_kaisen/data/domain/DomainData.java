package radon.jujutsu_kaisen.data.domain;


import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import radon.jujutsu_kaisen.DimensionManager;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.domain.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.packet.s2c.RemoveDomainInfoS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.UpdateDomainInfoS2CPacket;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

public class DomainData implements IDomainData {
    private final LinkedHashSet<DomainInfo> domains;
    private final Map<UUID, Vec3> spawns;
    private final Level level;
    @Nullable
    private ResourceKey<Level> original;

    private final DomainCarver carver;

    public DomainData(Level level) {
        this.level = level;

        this.domains = new LinkedHashSet<>();
        this.spawns = new HashMap<>();

        this.carver = new DomainCarver(this.domains);
    }

    @Override
    public void tick() {
        if (!(this.level instanceof ServerLevel serverLevel)) return;

        if (this.original == null) return;

        ServerLevel original = serverLevel.getServer().getLevel(this.original);

        if (original == null) return;

        this.domains.removeIf(info -> !(original.getEntity(info.identifier()) instanceof DomainExpansionEntity domain)
                || domain.isRemoved());

        this.carver.tick(serverLevel);

        List<Entity> entities = new ArrayList<>();
        serverLevel.getEntities().getAll().forEach(entities::add);

        Set<DomainExpansionEntity> domains = new HashSet<>();

        for (Entity entity : entities) {
            if (!(entity instanceof DomainExpansionEntity domain)) continue;

            domains.add(domain);
        }

        for (Entity entity : entities) {
            if (!(entity instanceof SimpleDomainEntity simple)) continue;

            for (DomainExpansionEntity domain : domains) {
                if (!domain.checkSureHitEffect()) continue;

                LivingEntity target = domain.getOwner();

                if (target == null) continue;

                IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) continue;

                ISkillData data = cap.getSkillData();

                float damage = (1 + data.getSkill(Skill.BARRIER)) * 0.01F;
                simple.setHealth(simple.getHealth() - damage);
            }
        }

        for (DomainExpansionEntity first : domains) {
            for (DomainExpansionEntity second : domains) {
                if (second == first) continue;

                if (first.shouldCollapse(second.getStrength())) {
                    first.discard();
                }
            }
        }

        if (this.domains.isEmpty()) {
            for (Entity entity : entities) {
                this.tryTeleportBack(entity);
            }
            DimensionManager.remove(serverLevel);
        }
    }

    @Override
    public @Nullable ResourceKey<Level> getOriginal() {
        return this.original;
    }

    @Override
    public void setOriginal(@Nullable ResourceKey<Level> original) {
        this.original = original;
    }

    @Override
    public boolean hasDomain(UUID owner) {
        for (DomainInfo info : this.domains) {
            if (info.owner().equals(owner)) return true;
        }
        return false;
    }

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
    public void remove(UUID identifier) {
        this.domains.removeIf(info -> info.identifier().equals(identifier));

        PacketDistributor.sendToAllPlayers(new RemoveDomainInfoS2CPacket(this.level.dimension(), identifier));
    }

    @Override
    public Set<DomainInfo> getDomains() {
        return this.domains;
    }

    @Override
    public boolean tryTeleportBack(Entity entity) {
        if (!(this.level instanceof ServerLevel serverLevel)) return false;

        if (this.original == null) return false;

        ServerLevel original = serverLevel.getServer().getLevel(this.original);

        if (original == null) return false;

        UUID identifier = entity.getUUID();

        if (!this.spawns.containsKey(identifier)) return false;

        Vec3 pos = this.spawns.get(identifier);

        return entity.teleportTo(original, pos.x, pos.y, pos.z, Set.of(), entity.getYRot(), entity.getXRot());
    }

    @Override
    public void addSpawn(UUID identifier, Vec3 pos) {
        this.spawns.put(identifier, pos);
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

        ListTag spawnsTag = new ListTag();

        for (Map.Entry<UUID, Vec3> entry : this.spawns.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.putUUID("identifier", entry.getKey());
            data.put("spawn", Vec3.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).getOrThrow());
            spawnsTag.add(data);
        }
        nbt.put("spawns", spawnsTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains("original")) {
            this.original = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("original")));
        }

        this.domains.clear();

        for (Tag key : nbt.getList("domains", Tag.TAG_COMPOUND)) {
            this.domains.add(DomainInfo.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), key).getOrThrow());
        }

        this.spawns.clear();

        for (Tag key : nbt.getList("spawns", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;
            this.spawns.put(data.getUUID("identifier"), Vec3.CODEC.parse(NbtOps.INSTANCE,
                    data.get("spawn")).getOrThrow());
        }
    }
}
