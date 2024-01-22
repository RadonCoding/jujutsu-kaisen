package radon.jujutsu_kaisen.client.visual.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

public interface IVisual {
    boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData data);

    void tick(LivingEntity entity, ClientVisualHandler.ClientData data);
}
