package radon.jujutsu_kaisen.tags;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKBlockTags {
    public static final TagKey<Block> BARRIER = BlockTags.create(new ResourceLocation(JujutsuKaisen.MOD_ID, "barrier"));
    public static final TagKey<Block> INCORRECT_FOR_CURSED_TOOL = BlockTags.create(new ResourceLocation(JujutsuKaisen.MOD_ID, "incorrect_for_cursed_tool"));
}
