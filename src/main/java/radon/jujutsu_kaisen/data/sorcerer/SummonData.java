package radon.jujutsu_kaisen.data.sorcerer;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;

import java.util.UUID;

public class SummonData {
    private final UUID uuid;
    private final ResourceKey<Level> dimension;
    private Long chunkPos;

    public SummonData(Entity entity) {
        this.uuid = entity.getUUID();
        this.dimension = entity.level().dimension();
        this.chunkPos = entity.chunkPosition().toLong();
    }

    public SummonData(CompoundTag nbt) {
        this.uuid = nbt.getUUID("uuid");
        this.dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("dimension")));
        this.chunkPos = nbt.getLong("chunk_pos");
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public Long getChunkPos() {
        return this.chunkPos;
    }

    public void setChunkPos(long chunkPos) {
        this.chunkPos = chunkPos;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("uuid", this.uuid);
        nbt.putString("dimension", this.dimension.location().toString());
        nbt.putLong("chunk_pos", this.chunkPos);
        return nbt;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SummonData other)) {
            return false;
        }
        return this.getUUID() == other.getUUID();
    }

    @Override
    public int hashCode() {
        return this.getUUID().hashCode();
    }
}
