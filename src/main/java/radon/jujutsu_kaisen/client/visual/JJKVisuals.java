package radon.jujutsu_kaisen.client.visual;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.client.visual.visual.BlueFistsVisual;
import radon.jujutsu_kaisen.client.visual.visual.CursedEnergyVisual;
import radon.jujutsu_kaisen.client.visual.visual.IdleTransfigurationVisual;
import radon.jujutsu_kaisen.client.visual.visual.TransfiguredSoulVisual;

import java.util.function.Supplier;

public class JJKVisuals {
    public static DeferredRegister<IVisual> VISUALS = DeferredRegister.create(
            new ResourceLocation(JujutsuKaisen.MOD_ID, "visual"), JujutsuKaisen.MOD_ID);
    public static Supplier<IForgeRegistry<IVisual>> VISUAL_REGISTRY =
            VISUALS.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<IVisual> CURSED_ENERGY = VISUALS.register("cursed_energy", CursedEnergyVisual::new);
    public static RegistryObject<IVisual> BLUE_FISTS = VISUALS.register("blue_fists", BlueFistsVisual::new);
    public static RegistryObject<IVisual> IDLE_TRANSFIGURATION = VISUALS.register("idle_transfiguration", IdleTransfigurationVisual::new);
    public static RegistryObject<IVisual> TRANSFIGURED_SOUL = VISUALS.register("transfigured_soul", TransfiguredSoulVisual::new);
}
