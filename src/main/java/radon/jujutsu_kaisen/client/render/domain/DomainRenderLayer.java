package radon.jujutsu_kaisen.client.render.domain;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public abstract class DomainRenderLayer {
    protected abstract ResourceLocation getTexture();
}
