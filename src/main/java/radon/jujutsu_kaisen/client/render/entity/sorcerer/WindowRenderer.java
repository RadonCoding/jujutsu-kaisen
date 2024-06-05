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
import radon.jujutsu_kaisen.client.model.entity.MegumiFushiguroModel;
import radon.jujutsu_kaisen.client.model.entity.WindowModel;
import radon.jujutsu_kaisen.entity.sorcerer.WindowEntity;

public class WindowRenderer extends HumanoidMobRenderer<WindowEntity, WindowModel> {
    public WindowRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new WindowModel(pContext.bakeLayer(WindowModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(WindowModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(WindowModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull WindowEntity pEntity) {
        return new ResourceLocation(JujutsuKaisen.MOD_ID, String.format("textures/entity/window_%d.png", pEntity.getVariant()));
    }
}

