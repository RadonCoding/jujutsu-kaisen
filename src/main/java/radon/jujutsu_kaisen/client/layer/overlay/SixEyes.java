package radon.jujutsu_kaisen.client.layer.overlay;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class SixEyes extends Overlay {
    @Override
    public RenderType getRenderType() {
        return RenderType.eyes(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/six_eyes.png"));
    }

    @Override
    public int getPackedLight() {
        return 15728640;
    }
}
