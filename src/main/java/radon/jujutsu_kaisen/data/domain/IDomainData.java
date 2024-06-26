package radon.jujutsu_kaisen.data.domain;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

import java.util.Set;
import java.util.UUID;

public interface IDomainData extends INBTSerializable<CompoundTag> {
    void tick();

    @Nullable ResourceKey<Level> getOriginal();

    void setOriginal(ResourceKey<Level> original);

    boolean hasDomain(UUID owner);

    void update(DomainExpansionEntity domain);

    void update(DomainInfo info);

    void remove(UUID identifier);

    Set<DomainInfo> getDomains();

    void addSpawn(UUID identifier, Vec3 pos);
}
