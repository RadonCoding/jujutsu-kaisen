package radon.jujutsu_kaisen.client.post;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Credit: https://github.com/M-Marvin/MCMOD-HoloStructures-V2/blob/main-1.20.4/HoloStructures-1.20/src/main/java/de/m_marvin/holostruct/client/rendering/posteffect/SelectivePostChain.java
public class SelectivePostChain extends PostChain {
    public SelectivePostChain(TextureManager pTextureManager, ResourceManager pResourceManager, RenderTarget pScreenTarget, ResourceLocation pName) throws IOException, JsonSyntaxException {
        super(pTextureManager, pResourceManager, pScreenTarget, pName);
    }

    public List<AbstractUniform> getUniforms(String name) {
        List<AbstractUniform> uniforms = new ArrayList<>();

        for (PostPass pass : this.passes) {
            uniforms.add(pass.getEffect().safeGetUniform(name));
        }
        return uniforms;
    }

    public void setMatrices(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        for (PostPass pass : this.passes) {
            ((SelectivePostPass) pass).setMatrices(modelViewMatrix, projectionMatrix);
        }
    }

    @Override
    public @NotNull PostPass addPass(@NotNull String pName, @NotNull RenderTarget pInTarget, @NotNull RenderTarget pOutTarget, boolean pUseLinearFilter) throws IOException {
        SelectivePostPass pass = new SelectivePostPass(this.resourceProvider, pName, pInTarget, pOutTarget, pUseLinearFilter);
        this.passes.add(this.passes.size(), pass);
        return pass;
    }
}