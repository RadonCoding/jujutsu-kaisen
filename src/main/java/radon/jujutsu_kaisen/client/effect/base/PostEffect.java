package radon.jujutsu_kaisen.client.effect.base;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.mixin.client.IPostChainAccessor;

import java.io.IOException;

public abstract class PostEffect implements ResourceManagerReloadListener {
    private Object postChain;

    protected abstract ResourceLocation getEffect();

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager pResourceManager) {
        Minecraft mc = Minecraft.getInstance();

        if (this.postChain != null) {
            ((PostChain) this.postChain).close();
        }

        try {
            if (mc.isSameThread()) {
                this.postChain = new PostChain(mc.getTextureManager(), pResourceManager, mc.getMainRenderTarget(), this.getEffect());
                ((PostChain) this.postChain).resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            }
        } catch (JsonSyntaxException | IOException ignored) {}
    }

    public void resize(int width, int heigtht) {
        if (postChain != null) {
            ((PostChain) postChain).resize(width, heigtht);
        }
    }

    public abstract boolean shouldRender(LocalPlayer player);

    protected void applyUniforms(PostPass pass) {
    }

    public void render(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        if (this.postChain == null) {
            this.onResourceManagerReload(mc.getResourceManager());
        }

        for (PostPass pass : ((IPostChainAccessor) this.postChain).getPassesAccessor()) {
            this.applyUniforms(pass);
        }
        ((PostChain) this.postChain).process(partialTicks);
    }
}