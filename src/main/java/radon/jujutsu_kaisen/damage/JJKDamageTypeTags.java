package radon.jujutsu_kaisen.damage;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKDamageTypeTags {
    public static TagKey<DamageType> SOUL = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "soul"));
}
