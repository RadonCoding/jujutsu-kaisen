package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class DefaultDomainRenderer extends TextureDomainRenderer {
    @Override
    protected ResourceLocation getTexture() {
        return MissingTextureAtlasSprite.getLocation();
    }
}
