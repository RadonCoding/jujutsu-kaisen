package radon.jujutsu_kaisen.client.layer.overlay;

import net.minecraft.client.renderer.RenderType;

public abstract class RenderableOverlay extends Overlay {
    public abstract RenderType getRenderType();
    public abstract int getPackedLight();
}
