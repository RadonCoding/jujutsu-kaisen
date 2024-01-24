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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class JJKVisuals {
    // TODO: Make this List<Supplier<IVisual>> and make each entity have their own instance
    public static List<IVisual> VISUALS = new ArrayList<>();

    static {
        VISUALS.add(new CursedEnergyVisual());
        VISUALS.add(new BlueFistsVisual());
        VISUALS.add(new IdleTransfigurationVisual());
        VISUALS.add(new TransfiguredSoulVisual());
    }
}
