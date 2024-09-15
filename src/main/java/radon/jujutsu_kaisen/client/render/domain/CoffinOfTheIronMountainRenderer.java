package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class CoffinOfTheIronMountainRenderer extends DomainRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/domain/coffin_of_the_iron_mountain.png");

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
