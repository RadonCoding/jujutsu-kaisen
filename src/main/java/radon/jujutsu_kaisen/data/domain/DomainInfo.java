package radon.jujutsu_kaisen.data.domain;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;

import java.util.UUID;

public record DomainInfo(UUID owner, UUID identifier, DomainExpansion ability, float strength, int time) {
    public static Codec<DomainInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(DomainInfo::owner),
            UUIDUtil.CODEC.fieldOf("identifier").forGetter(DomainInfo::identifier),
            JJKAbilities.ABILITY_REGISTRY.byNameCodec()
                    .xmap(ability -> (DomainExpansion) ability, ability -> ability)
                    .fieldOf("ability").forGetter(DomainInfo::ability),
            Codec.FLOAT.fieldOf("strength").forGetter(DomainInfo::strength),
            Codec.INT.fieldOf("time").forGetter(DomainInfo::time)
    ).apply(instance, DomainInfo::new));
    public static StreamCodec<RegistryFriendlyByteBuf, DomainInfo> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            DomainInfo::owner,
            UUIDUtil.STREAM_CODEC,
            DomainInfo::identifier,
            ByteBufCodecs.registry(JJKAbilities.ABILITY_KEY)
                    .map(ability -> (DomainExpansion) ability, ability -> ability),
            DomainInfo::ability,
            ByteBufCodecs.FLOAT,
            DomainInfo::strength,
            ByteBufCodecs.INT,
            DomainInfo::time,
            DomainInfo::new
    );

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DomainInfo other)) return false;

        return this.identifier.equals(other.identifier);
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }
}
