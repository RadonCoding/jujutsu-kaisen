package radon.jujutsu_kaisen.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKBlockTags {
    public static final TagKey<Block> DOMAIN = BlockTags.create(new ResourceLocation(JujutsuKaisen.MOD_ID, "domain"));
}
