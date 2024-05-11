package radon.jujutsu_kaisen.data.curse_manipulation;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryCodecs;
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
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.data.ten_shadows.Adaptation;

import java.util.Objects;

public class AbsorbedCurse {
    public static Codec<AbsorbedCurse> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.FLAT_CODEC.fieldOf("name").forGetter(AbsorbedCurse::getName),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(AbsorbedCurse::getType),
            CompoundTag.CODEC.fieldOf("data").forGetter(AbsorbedCurse::getData),
            ExtraCodecs.GAME_PROFILE.fieldOf("profile").forGetter(AbsorbedCurse::getProfile)
    ).apply(instance, AbsorbedCurse::new));
    public static StreamCodec<RegistryFriendlyByteBuf, AbsorbedCurse> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC,
            AbsorbedCurse::getName,
            ByteBufCodecs.registry(Registries.ENTITY_TYPE),
            AbsorbedCurse::getType,
            ByteBufCodecs.COMPOUND_TAG,
            AbsorbedCurse::getData,
            ByteBufCodecs.GAME_PROFILE,
            AbsorbedCurse::getProfile,
            AbsorbedCurse::new
    );

    private final Component name;
    private final EntityType<?> type;
    private final CompoundTag data;

    @Nullable
    private GameProfile profile;

    public AbsorbedCurse(Component name, EntityType<?> type, CompoundTag data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public AbsorbedCurse(Component name, EntityType<?> type, CompoundTag data, @Nullable GameProfile profile) {
        this(name, type, data);

        this.profile = profile;
    }

    public Component getName() {
        return this.name;
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public CompoundTag getData() {
        return this.data;
    }

    @Nullable
    public GameProfile getProfile() {
        return this.profile;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbsorbedCurse other)) return false;

        return this.name == other.name && this.type == other.type && this.data.equals(other.data) &&
                Objects.equals(this.profile, other.profile);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.name.hashCode();
        result = prime * result + this.type.hashCode();
        result = prime * result + this.data.hashCode();

        if (this.profile != null) {
            result = prime * result + this.profile.hashCode();
        }
        return result;
    }
}
