package radon.jujutsu_kaisen.tags;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKBlockTags {
    public static final TagKey<Block> INCORRECT_FOR_CURSED_TOOL = BlockTags.create(new ResourceLocation(JujutsuKaisen.MOD_ID, "incorrect_for_cursed_tool"));
}
