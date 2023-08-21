package radon.jujutsu_kaisen.world.gen.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddItemsModifier extends LootModifier {
    public static final Codec<AddItemsModifier> CODEC = RecordCodecBuilder.create(codec -> codecStart(codec)
            .and(ForgeRegistries.ITEMS.getCodec().listOf().fieldOf("items").forGetter(x -> x.items))
            .apply(codec, AddItemsModifier::new));

    private final List<Item> items;

    public AddItemsModifier(LootItemCondition[] conditionsIn, List<Item> items) {
        super(conditionsIn);

        this.items = items;
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (Item item : this.items) {
            if (context.getRandom().nextInt(10) == 0) {
                generatedLoot.add(new ItemStack(item));
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}