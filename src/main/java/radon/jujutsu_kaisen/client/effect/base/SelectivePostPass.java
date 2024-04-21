package radon.jujutsu_kaisen.client.effect.base;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

import java.io.IOException;

// Credit: https://github.com/M-Marvin/MCMOD-HoloStructures-V2/blob/main-1.20.4/HoloStructures-1.20/src/main/java/de/m_marvin/holostruct/client/rendering/posteffect/SelectivePostPass.java
public class SelectivePostPass extends PostPass {
    public SelectivePostPass(ResourceManager pResourceManager, String pName, RenderTarget pInTarget, RenderTarget pOutTarget) throws IOException {
        super(pResourceManager, pName, pInTarget, pOutTarget);
    }

    @Override
    public void process(float pPartialTicks) {
        this.inTarget.unbindWrite();
        float f = (float) this.outTarget.width;
        float f1 = (float) this.outTarget.height;
        RenderSystem.viewport(0, 0, (int)f, (int)f1);
        this.effect.setSampler("DiffuseSampler", this.inTarget::getColorTextureId);

        /* HS2 Modification: Pass depth buffer to post effect shader */
        this.effect.setSampler("DepthSampler", this.inTarget::getDepthTextureId);

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

        /* HS2 Modification: Reset BlendMode#lastApplied since it for some reason gets not reset automatically */
        new BlendMode().apply();

        this.effect.apply();
        this.outTarget.bindWrite(false);

        /* HS2 Modification: Don't use GL_ALWAYS, we want depth testing to work when adding stuff */
        RenderSystem.depthFunc(GL30.GL_LEQUAL);
        RenderSystem.depthMask(true);

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
