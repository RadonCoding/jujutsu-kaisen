package radon.jujutsu_kaisen.client.effect;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ImpactFrameHandler;
import radon.jujutsu_kaisen.client.effect.base.PostEffect;
import radon.jujutsu_kaisen.client.effect.base.SelectivePostChain;

import java.io.IOException;


public class ImpactFramePostEffect extends PostEffect {
    private static final ResourceLocation EFFECT = new ResourceLocation(JujutsuKaisen.MOD_ID, "shaders/post/impact_frame.json");

    @Override
    public PostChain create() {
        Minecraft mc = Minecraft.getInstance();

        try {
            PostChain chain = new SelectivePostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), EFFECT);
            PostEffectHandler.preparePostEffect(chain);
            return chain;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RenderTarget getCustomTarget() {
        return this.getChain().getTempTarget("final");
    }

    @Override
    public boolean shouldRender() {
        return ImpactFrameHandler.isActive();
    }
}