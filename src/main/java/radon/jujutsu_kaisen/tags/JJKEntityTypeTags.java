package radon.jujutsu_kaisen.tags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import radon.jujutsu_kaisen.JujutsuKaisen;

import javax.swing.text.html.parser.Entity;

public class JJKEntityTypeTags {
    public static final TagKey<EntityType<?>> FORCE_FEEDABLE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "force_feedable"));
    public static final TagKey<EntityType<?>> SPAWNABLE_CURSE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "spawnable_curse"));
}
