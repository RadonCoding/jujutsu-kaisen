package radon.jujutsu_kaisen.client.effect.base;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

// Credit: https://github.com/M-Marvin/MCMOD-HoloStructures-V2/blob/main-1.20.4/HoloStructures-1.20/src/main/java/de/m_marvin/holostruct/client/rendering/posteffect/SelectivePostChain.java
public class SelectivePostChain extends PostChain {
    public SelectivePostChain(TextureManager pTextureManager, ResourceManager pResourceManager, RenderTarget pScreenTarget, ResourceLocation pName) throws IOException, JsonSyntaxException {
        super(pTextureManager, pResourceManager, pScreenTarget, pName);
    }

    @Override
    public @NotNull PostPass addPass(@NotNull String pProgramName, @NotNull RenderTarget pFramebuffer, @NotNull RenderTarget pFramebufferOut) throws IOException {
        SelectivePostPass pass = new SelectivePostPass(this.resourceManager, pProgramName, pFramebuffer, pFramebufferOut);
        this.passes.add(this.passes.size(), pass);
        return pass;
    }
}
