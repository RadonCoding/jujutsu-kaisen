package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.entity.TojiZeninModel;
import radon.jujutsu_kaisen.entity.sorcerer.TojiZeninEntity;

public class TojiZeninRenderer  extends HumanoidMobRenderer<TojiZeninEntity, TojiZeninModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/toji_zenin.png");

    public TojiZeninRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new TojiZeninModel(pContext.bakeLayer(TojiZeninModel.LAYER)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TojiZeninEntity pEntity) {
        return TEXTURE;
    }
}
