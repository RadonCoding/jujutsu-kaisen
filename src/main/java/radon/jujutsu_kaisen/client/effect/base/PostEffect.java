package radon.jujutsu_kaisen.client.effect.base;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

public abstract class PostEffect {
    private final Lazy<PostChain> chain = Lazy.of(this::create);

    public PostChain getChain() {
        return this.chain.get();
    }

    protected abstract PostChain create();

    public abstract boolean shouldRender();

    @Nullable
    public RenderTarget getCustomTarget() {
        return null;
    }
}