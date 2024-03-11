package radon.jujutsu_kaisen.client.visual;

import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.client.visual.visual.*;

import java.util.ArrayList;
import java.util.List;

public class JJKVisuals {
    // TODO: Make this List<Supplier<IVisual>> and make each entity have their own instance
    public static List<IVisual> VISUALS = new ArrayList<>();

    static {
        VISUALS.add(new CursedEnergyVisual());
        VISUALS.add(new BlueFistsVisual());
        VISUALS.add(new AzureGlideVisual());
        VISUALS.add(new IdleTransfigurationVisual());
        VISUALS.add(new PerfectBodyVisual());
    }
}
