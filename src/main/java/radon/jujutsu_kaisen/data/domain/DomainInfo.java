package radon.jujutsu_kaisen.data.domain;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.network.codec.JJKByteBufCodecs;

import java.util.Objects;
import java.util.UUID;

public record DomainInfo(UUID owner, UUID identifier, Ability ability, float strength) {
    public static Codec<DomainInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(DomainInfo::owner),
            UUIDUtil.CODEC.fieldOf("identifier").forGetter(DomainInfo::identifier),
            JJKAbilities.ABILITY_REGISTRY.byNameCodec().fieldOf("ability").forGetter(DomainInfo::ability),
            Codec.FLOAT.fieldOf("strength").forGetter(DomainInfo::strength)
    ).apply(instance, DomainInfo::new));
    public static StreamCodec<RegistryFriendlyByteBuf, DomainInfo> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            DomainInfo::owner,
            UUIDUtil.STREAM_CODEC,
            DomainInfo::identifier,
            ByteBufCodecs.registry(JJKAbilities.ABILITY_KEY),
            DomainInfo::ability,
            ByteBufCodecs.FLOAT,
            DomainInfo::strength,
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
