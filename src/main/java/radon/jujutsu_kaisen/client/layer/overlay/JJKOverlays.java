package radon.jujutsu_kaisen.client.layer.overlay;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.function.Supplier;

public class JJKOverlays {
    public static DeferredRegister<Overlay> OVERLAYS = DeferredRegister.create(
            new ResourceLocation(JujutsuKaisen.MOD_ID, "overlay"), JujutsuKaisen.MOD_ID);
    public static Supplier<IForgeRegistry<Overlay>> OVERLAY_REGISTRY =
            OVERLAYS.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<Overlay> SIX_EYES = OVERLAYS.register("six_eyes", SixEyesOverlay::new);
    public static RegistryObject<Overlay> BLUE_FISTS = OVERLAYS.register("blue_fists", BlueFistsOverlay::new);

    public static ResourceLocation getKey(Overlay overlay) {
        return OVERLAY_REGISTRY.get().getKey(overlay);
    }

    public static Overlay getValue(ResourceLocation key) {
        return OVERLAY_REGISTRY.get().getValue(key);
    }
}
