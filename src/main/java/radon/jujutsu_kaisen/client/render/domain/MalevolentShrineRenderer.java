package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class MalevolentShrineRenderer extends DomainRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/domain/malevolent_shrine.png");

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
