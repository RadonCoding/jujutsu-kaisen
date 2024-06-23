package radon.jujutsu_kaisen.client.post;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.util.MathUtil;

import java.io.IOException;

import static org.lwjgl.opengl.GL11C.*;

// Credit: https://github.com/M-Marvin/MCMOD-HoloStructures-V2/blob/main-1.20.4/HoloStructures-1.20/src/main/java/de/m_marvin/holostruct/client/rendering/posteffect/SelectivePostPass.java
public class SelectivePostPass extends PostPass {
    private Matrix4f invModelViewMat;
    private Matrix4f invProjMat;

    public SelectivePostPass(ResourceProvider pResourceProvider, String pName, RenderTarget pInTarget, RenderTarget pOutTarget, boolean pUseLinearFilter) throws IOException {
        super(pResourceProvider, pName, pInTarget, pOutTarget, pUseLinearFilter);
    }

    public void setMatrices(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        this.invModelViewMat = MathUtil.inverse(modelViewMatrix);
        this.invProjMat = MathUtil.inverse(projectionMatrix);
    }

    @Override
    public void process(float pPartialTicks) {
        this.inTarget.unbindWrite();
        float f = (float) this.outTarget.width;
        float f1 = (float) this.outTarget.height;
        RenderSystem.viewport(0, 0, (int) f, (int) f1);
        this.effect.setSampler("DiffuseSampler", this.inTarget::getColorTextureId);

        for(int i = 0; i < this.auxAssets.size(); ++i) {
            this.effect.setSampler(this.auxNames.get(i), this.auxAssets.get(i));
            this.effect.safeGetUniform("AuxSize" + i).set((float) this.auxWidths.get(i), (float) this.auxHeights.get(i));
        }

        this.effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
        this.effect.safeGetUniform("InSize").set((float) this.inTarget.width, (float) this.inTarget.height);
        this.effect.safeGetUniform("OutSize").set(f, f1);
        this.effect.safeGetUniform("Time").set(pPartialTicks);
        Minecraft mc = Minecraft.getInstance();
        this.effect.safeGetUniform("ScreenSize").set((float) mc.getWindow().getWidth(), (float) mc.getWindow().getHeight());

        /* JJK Modification: Pass inverse model view matrix */
        this.effect.safeGetUniform("InvModelViewMat").set(this.invModelViewMat);

        /* JJK Modification: Pass inverse projection matrix */
        this.effect.safeGetUniform("InvProjMat").set(this.invProjMat);

        new BlendMode().apply();

        this.effect.apply();
        this.outTarget.bindWrite(false);
        RenderSystem.depthFunc(519);
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        builder.vertex(0.0D, 0.0D, 500.0D).endVertex();
        builder.vertex(f, 0.0D, 500.0D).endVertex();
        builder.vertex(f, f1, 500.0D).endVertex();
        builder.vertex(0.0D, f1, 500.0D).endVertex();
        BufferUploader.draw(builder.end());
        RenderSystem.depthFunc(515);
        this.effect.clear();
        this.outTarget.unbindWrite();
        this.inTarget.unbindRead();

        for (Object object : this.auxAssets) {
            if (!(object instanceof RenderTarget target)) continue;

            target.unbindRead();
        }
    }
}