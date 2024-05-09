package radon.jujutsu_kaisen.imbuement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;

public record Imbuement(Ability ability, int imbuement) {
    public static Codec<Imbuement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            JJKAbilities.ABILITY_REGISTRY.byNameCodec().fieldOf("ability").forGetter(Imbuement::ability),
            Codec.INT.fieldOf("imbuement").forGetter(Imbuement::imbuement)
    ).apply(instance, Imbuement::new));
    public static StreamCodec<RegistryFriendlyByteBuf, Imbuement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(JJKAbilities.ABILITY_KEY),
            Imbuement::ability,
            ByteBufCodecs.INT,
            Imbuement::imbuement,
            Imbuement::new
    );
}
