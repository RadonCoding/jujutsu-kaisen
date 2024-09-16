package radon.jujutsu_kaisen.client.render.domain;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.util.RenderUtil;

public class CumRenderLayer extends DomainRenderLayer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/domain/cum.png");

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
