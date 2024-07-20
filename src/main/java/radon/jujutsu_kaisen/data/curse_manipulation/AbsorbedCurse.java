package radon.jujutsu_kaisen.data.curse_manipulation;


import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;
import java.util.Optional;

public record AbsorbedCurse(Component name, EntityType<?> type, CompoundTag data, Optional<GameProfile> profile) {
    public static Codec<AbsorbedCurse> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.FLAT_CODEC.fieldOf("name").forGetter(AbsorbedCurse::name),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(AbsorbedCurse::type),
            CompoundTag.CODEC.fieldOf("data").forGetter(AbsorbedCurse::data),
            ExtraCodecs.GAME_PROFILE.optionalFieldOf("profile").forGetter(AbsorbedCurse::profile)
    ).apply(instance, AbsorbedCurse::new));
    public static StreamCodec<RegistryFriendlyByteBuf, AbsorbedCurse> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC,
            AbsorbedCurse::name,
            ByteBufCodecs.registry(Registries.ENTITY_TYPE),
            AbsorbedCurse::type,
            ByteBufCodecs.COMPOUND_TAG,
            AbsorbedCurse::data,
            ByteBufCodecs.optional(ByteBufCodecs.GAME_PROFILE),
            AbsorbedCurse::profile,
            AbsorbedCurse::new
    );

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbsorbedCurse other)) return false;

        return this.name == other.name && this.type == other.type && this.data.equals(other.data) &&
                Objects.equals(this.profile, other.profile);
    }

}
