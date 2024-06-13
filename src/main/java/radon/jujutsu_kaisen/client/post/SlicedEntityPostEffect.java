package radon.jujutsu_kaisen.client.post;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class SlicedEntityPostEffect extends PostEffect {
    private static final ResourceLocation EFFECT = new ResourceLocation(JujutsuKaisen.MOD_ID, "shaders/post/sliced_entity.json");

    @Override
    protected ResourceLocation getEffect() {
        return EFFECT;
    }

    @Nullable
    @Override
    public RenderTarget getCustomTarget() {
        return this.getPostChain().getTempTarget(String.format("%s:sliced_entity", JujutsuKaisen.MOD_ID));
    }
}
