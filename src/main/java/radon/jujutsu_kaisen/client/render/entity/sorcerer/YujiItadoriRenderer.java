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
import radon.jujutsu_kaisen.client.model.entity.YujiItadoriModel;
import radon.jujutsu_kaisen.entity.sorcerer.YujiItadoriEntity;

public class YujiItadoriRenderer extends HumanoidMobRenderer<YujiItadoriEntity, YujiItadoriModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/yuji_itadori.png");

    public YujiItadoriRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new YujiItadoriModel(pContext.bakeLayer(YujiItadoriModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(YujiItadoriModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(YujiItadoriModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull YujiItadoriEntity pEntity) {
        return TEXTURE;
    }
}
