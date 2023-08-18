package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.entity.MegunaRyomenModel;
import radon.jujutsu_kaisen.entity.sorcerer.MegunaRyomenEntity;

public class MegunaRyomenRenderer extends HumanoidMobRenderer<MegunaRyomenEntity, MegunaRyomenModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/meguna_ryomen.png");

    public MegunaRyomenRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new MegunaRyomenModel(pContext.bakeLayer(MegunaRyomenModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(MegunaRyomenModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(MegunaRyomenModel.OUTER_LAYER)), pContext.getModelManager()));
    }
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MegunaRyomenEntity pEntity) {
        return TEXTURE;
    }
}
