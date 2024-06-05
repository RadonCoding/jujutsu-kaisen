package radon.jujutsu_kaisen.client.visual.base;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

public interface IOverlay {
    boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData client);

    <T extends LivingEntity> void render(T entity, ClientVisualHandler.ClientData client, ResourceLocation texture, EntityModel<T> model, PoseStack poseStack, MultiBufferSource buffer, float partialTicks, int packedLight);
}
