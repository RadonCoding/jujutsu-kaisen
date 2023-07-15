package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.YutaOkkotsuModel;
import radon.jujutsu_kaisen.entity.sorcerer.YutaOkkotsuEntity;

public class YutaOkkotsuRenderer extends HumanoidMobRenderer<YutaOkkotsuEntity, YutaOkkotsuModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/yuta_okkotsu.png");

    public YutaOkkotsuRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new YutaOkkotsuModel(pContext.bakeLayer(YutaOkkotsuModel.LAYER)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull YutaOkkotsuEntity pEntity) {
        return TEXTURE;
    }
}
