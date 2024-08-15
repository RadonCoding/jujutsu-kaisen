package radon.jujutsu_kaisen.client.post;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;

import javax.annotation.Nullable;
import java.io.IOException;

public abstract class PostEffect {
    @Nullable
    private SelectivePostChain postChain;

    public SelectivePostChain getPostChain() {
        if (this.postChain == null) {
            Minecraft mc = Minecraft.getInstance();

            try {
                this.postChain = new SelectivePostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), this.getEffect());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this.postChain;
    }

    protected abstract ResourceLocation getEffect();

    public RenderTarget getCustomTarget() {
        return this.getPostChain().getTempTarget(String.format("%s:target", JujutsuKaisen.MOD_ID));
    }
}