package radon.jujutsu_kaisen.tags;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKStructureTags {
    public static final TagKey<Structure> HAS_SORCERERS = TagKey.create(Registries.STRUCTURE, new ResourceLocation(JujutsuKaisen.MOD_ID, "has_sorcerers"));
    public static final TagKey<Structure> IS_MISSION = TagKey.create(Registries.STRUCTURE, new ResourceLocation(JujutsuKaisen.MOD_ID, "is_mission"));
}
