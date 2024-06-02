package radon.jujutsu_kaisen.data.domain;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IDomainData extends INBTSerializable<CompoundTag> {
    @Nullable ResourceKey<Level> getOriginal();

    void init(ResourceKey<Level> original);

    boolean hasDomain(UUID owner);

    void update(DomainExpansionEntity domain);

    void update(DomainInfo info);

    Set<DomainInfo> getDomains();
}
