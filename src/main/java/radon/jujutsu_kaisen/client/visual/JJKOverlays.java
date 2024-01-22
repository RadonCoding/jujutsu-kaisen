package radon.jujutsu_kaisen.client.visual;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.visual.base.IOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.CursedSpeechOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.PerfectBodyOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.SixEyesOverlay;

import java.util.function.Supplier;

public class JJKOverlays {
    public static DeferredRegister<IOverlay> OVERLAYS = DeferredRegister.create(
            new ResourceLocation(JujutsuKaisen.MOD_ID, "overlay"), JujutsuKaisen.MOD_ID);
    public static Supplier<IForgeRegistry<IOverlay>> OVERLAY_REGISTRY =
            OVERLAYS.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<IOverlay> SIX_EYES = OVERLAYS.register("six_eyes", SixEyesOverlay::new);
    public static RegistryObject<IOverlay> CURSED_SPEECH = OVERLAYS.register("cursed_speech", CursedSpeechOverlay::new);
    public static RegistryObject<IOverlay> PERFECT_BODY = OVERLAYS.register("perfect_body", PerfectBodyOverlay::new);
}
