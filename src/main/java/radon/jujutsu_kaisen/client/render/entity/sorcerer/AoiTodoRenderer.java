package radon.jujutsu_kaisen.client.render.entity.sorcerer;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.AoiTodoModel;
import radon.jujutsu_kaisen.client.model.entity.HajimeKashimoModel;
import radon.jujutsu_kaisen.entity.sorcerer.AoiTodoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.HajimeKashimoEntity;

public class AoiTodoRenderer extends HumanoidMobRenderer<AoiTodoEntity, AoiTodoModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/aoi_todo.png");

    public AoiTodoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new AoiTodoModel(pContext.bakeLayer(AoiTodoModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(AoiTodoModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(AoiTodoModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AoiTodoEntity pEntity) {
        return TEXTURE;
    }
}
