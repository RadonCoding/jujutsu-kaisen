package radon.jujutsu_kaisen.client.render.domain;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.util.RenderUtil;

public class UnlimitedVoidRenderer extends DomainRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/domain/unlimited_void.png");

    public UnlimitedVoidRenderer() {
        this.addLayer(new CumRenderLayer());
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
