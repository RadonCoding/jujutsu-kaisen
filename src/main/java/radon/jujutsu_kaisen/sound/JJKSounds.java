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
    public static final RegistryObject<SoundEvent> GUN = SOUNDS.register("gun", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "gun")));
    public static RegistryObject<SoundEvent> EXPLOSION = SOUNDS.register("explosion", () ->
                    SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "explosion")));
    public static RegistryObject<SoundEvent> WHEEL = SOUNDS.register("wheel", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "wheel")));
    public static RegistryObject<SoundEvent> WOLF_HOWLING = SOUNDS.register("wolf_howling", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(JujutsuKaisen.MOD_ID, "wolf_howling")));
}
