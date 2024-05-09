package radon.jujutsu_kaisen.item.armor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.imbuement.Imbuement;

import java.util.ArrayList;
import java.util.List;

public class InventoryCurseItems {
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

    private List<ItemStack> stacks = new ArrayList<>();
}
