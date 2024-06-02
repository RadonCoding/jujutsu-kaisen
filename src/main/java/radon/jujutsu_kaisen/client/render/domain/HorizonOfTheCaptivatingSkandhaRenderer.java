package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class HorizonOfTheCaptivatingSkandhaRenderer extends DomainRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/domain/horizon_of_the_captivating_skandha.png");

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
