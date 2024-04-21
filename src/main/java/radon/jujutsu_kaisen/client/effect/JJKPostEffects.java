package radon.jujutsu_kaisen.client.effect;


import radon.jujutsu_kaisen.client.effect.base.PostEffect;

import java.util.ArrayList;
import java.util.List;

public class JJKPostEffects {
    public static List<PostEffect> EFFECTS = new ArrayList<>();

    public static final PostEffect IMPACT_FRAME = new ImpactFramePostEffect();

    static {
        EFFECTS.add(new ScissorPostEffect());
        EFFECTS.add(IMPACT_FRAME);
    }
}