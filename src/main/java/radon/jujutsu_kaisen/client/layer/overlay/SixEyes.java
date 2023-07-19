package radon.jujutsu_kaisen.client.layer.overlay;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;

public class SixEyes extends Overlay {
    private static final RenderType RENDER_TYPE = JJKRenderTypes.eyes(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/six_eyes.png"));

    @Override
    public RenderType getRenderType() {
        return RENDER_TYPE;
    }

    @Override
    public int getPackedLight() {
        return 15728640;
    }
}
