package radon.jujutsu_kaisen.client.render.domain;


import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class AuthenticMutualLoveRenderer extends DomainRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/domain/authentic_mutual_love.png");

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
