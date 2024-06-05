package radon.jujutsu_kaisen.sound;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SLASH = SOUNDS.register("slash", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "slash")));
    public static DeferredHolder<SoundEvent, SoundEvent> WHEEL = SOUNDS.register("wheel", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "wheel")));
    public static DeferredHolder<SoundEvent, SoundEvent> WOLF_HOWLING = SOUNDS.register("wolf_howling", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "wolf_howling")));
    public static DeferredHolder<SoundEvent, SoundEvent> CURSED_SPEECH = SOUNDS.register("cursed_speech", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_speech")));
    public static DeferredHolder<SoundEvent, SoundEvent> CLAP = SOUNDS.register("clap", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "clap")));
    public static DeferredHolder<SoundEvent, SoundEvent> FOREST_SPIKES = SOUNDS.register("forest_spikes", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_spikes")));
    public static DeferredHolder<SoundEvent, SoundEvent> MALEVOLENT_SHRINE = SOUNDS.register("malevolent_shrine", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "malevolent_shrine")));
    public static DeferredHolder<SoundEvent, SoundEvent> ELECTRICITY = SOUNDS.register("electricity", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "electricity")));
    public static DeferredHolder<SoundEvent, SoundEvent> ELECTRIC_BLAST = SOUNDS.register("electric_blast", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "electric_blast")));
    public static DeferredHolder<SoundEvent, SoundEvent> CLEAVE = SOUNDS.register("cleave", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "cleave")));
    public static DeferredHolder<SoundEvent, SoundEvent> SWALLOW = SOUNDS.register("swallow", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "swallow")));
    public static DeferredHolder<SoundEvent, SoundEvent> DASH = SOUNDS.register("dash", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "dash")));
    public static DeferredHolder<SoundEvent, SoundEvent> HOLLOW_PURPLE_EXPLOSION = SOUNDS.register("hollow_purple_explosion", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "hollow_purple_explosion")));
    public static DeferredHolder<SoundEvent, SoundEvent> BLUE = SOUNDS.register("blue", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "blue")));
    public static DeferredHolder<SoundEvent, SoundEvent> RED_EXPLOSION = SOUNDS.register("red_explosion", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "red_explosion")));
    public static DeferredHolder<SoundEvent, SoundEvent> SLAM = SOUNDS.register("slam", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "slam")));
    public static DeferredHolder<SoundEvent, SoundEvent> FLAME_EXPLOSION = SOUNDS.register("flame_explosion", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "flame_explosion")));
    public static DeferredHolder<SoundEvent, SoundEvent> SHOOT = SOUNDS.register("shoot", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "shoot")));
    public static DeferredHolder<SoundEvent, SoundEvent> SPARK = SOUNDS.register("spark", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "spark")));
}
