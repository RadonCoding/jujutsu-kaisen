package radon.jujutsu_kaisen.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JujutsuKaisen.MOD_ID);

    public static final RegistryObject<SoundEvent> SLASH = SOUNDS.register("slash", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "slash")));
    public static RegistryObject<SoundEvent> EXPLOSION = SOUNDS.register("explosion", () ->
                    SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "explosion")));
    public static RegistryObject<SoundEvent> WHEEL = SOUNDS.register("wheel", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "wheel")));
    public static RegistryObject<SoundEvent> WOLF_HOWLING = SOUNDS.register("wolf_howling", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "wolf_howling")));
    public static RegistryObject<SoundEvent> CURSED_SPEECH = SOUNDS.register("cursed_speech", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_speech")));
    public static RegistryObject<SoundEvent> CLAP = SOUNDS.register("clap", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "clap")));
    public static RegistryObject<SoundEvent> FOREST_SPIKES = SOUNDS.register("forest_spikes", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "forest_spikes")));
    public static RegistryObject<SoundEvent> MALEVOLENT_SHRINE = SOUNDS.register("malevolent_shrine", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "malevolent_shrine")));
    public static RegistryObject<SoundEvent> ELECTRICITY = SOUNDS.register("electricity", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "electricity")));
    public static RegistryObject<SoundEvent> CLEAVE = SOUNDS.register("cleave", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "cleave")));
}
