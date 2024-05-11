package radon.jujutsu_kaisen.data.sorcerer;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class SummonData {
    private final int id;
    private final ResourceKey<Level> dimension;
    private Long chunkPos;

    public SummonData(Entity entity) {
        this.id = entity.getId();
        this.dimension = entity.level().dimension();
        this.chunkPos = entity.chunkPosition().toLong();
    }

    public SummonData(CompoundTag nbt) {
        this.id = nbt.getInt("id");
        this.dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("dimension")));
        this.chunkPos = nbt.getLong("chunk_pos");
    }

    public int getId() {
        return this.id;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public long getChunkPos() {
        return this.chunkPos;
    }

    public void setChunkPos(long chunkPos) {
        this.chunkPos = chunkPos;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("id", this.id);
        nbt.putString("dimension", this.dimension.location().toString());
        nbt.putLong("chunk_pos", this.chunkPos);
        return nbt;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SummonData other)) {
            return false;
        }
        return this.getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
