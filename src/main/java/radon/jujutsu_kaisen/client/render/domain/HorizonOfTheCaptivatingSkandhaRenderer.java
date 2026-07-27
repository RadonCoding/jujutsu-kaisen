package radon.jujutsu_kaisen.client.render.domain;

import com.mojang.blaze3d.pipeline.TextureTarget;
import radon.jujutsu_kaisen.client.SkyHandler;

public class HorizonOfTheCaptivatingSkandhaRenderer extends SkyDomainRenderer {
    @Override
    protected TextureTarget getTarget() {
        return SkyHandler.getHorizonOfTheCaptivatingSkandhaTarget();
    }
}
