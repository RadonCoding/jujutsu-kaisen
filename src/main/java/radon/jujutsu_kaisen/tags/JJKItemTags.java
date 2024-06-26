package radon.jujutsu_kaisen.tags;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKItemTags {
    public static final TagKey<Item> ALTAR = ItemTags.create(new ResourceLocation(JujutsuKaisen.MOD_ID, "altar"));
    public static final TagKey<Item> CURSED_TOOL = ItemTags.create(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_tool"));
    public static final TagKey<Item> CURSED_OBJECT = ItemTags.create(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_object"));
}
