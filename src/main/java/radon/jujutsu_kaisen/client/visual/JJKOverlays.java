package radon.jujutsu_kaisen.client.visual;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import radon.jujutsu_kaisen.client.visual.base.IOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.CursedSpeechOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.PerfectBodyOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.SixEyesOverlay;

import java.util.ArrayList;
import java.util.List;

public class JJKOverlays {
    // TODO: Make this List<Supplier<IOverlay>> and make each entity have their own instance
    public static List<IOverlay> OVERLAYS = new ArrayList<>();

    static {
        OVERLAYS.add(new SixEyesOverlay());
        OVERLAYS.add(new CursedSpeechOverlay());
        OVERLAYS.add(new PerfectBodyOverlay());
    }
}
