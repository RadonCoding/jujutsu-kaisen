package radon.jujutsu_kaisen.data.sorcerer;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class SummonData {
    private final UUID identifier;
    private Long chunkPos;

    public SummonData(Entity entity) {
        this.identifier = entity.getUUID();
        this.chunkPos = entity.chunkPosition().toLong();
    }

    public SummonData(CompoundTag nbt) {
        this.identifier = nbt.getUUID("identifier");
        this.chunkPos = nbt.getLong("chunk_pos");
    }

    public UUID getIdentifier() {
        return this.identifier;
    }

    public long getChunkPos() {
        return this.chunkPos;
    }

    public void setChunkPos(long chunkPos) {
        this.chunkPos = chunkPos;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("identifier", this.identifier);
        nbt.putLong("chunk_pos", this.chunkPos);
        return nbt;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SummonData other)) {
            return false;
        }
        return this.getIdentifier() == other.getIdentifier();
    }

    @Override
    public int hashCode() {
        return this.getIdentifier().hashCode();
    }
}
