package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.entity.SukunaRyomenModel;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;

public class SukunaRyomenRenderer extends HumanoidMobRenderer<SukunaEntity, SukunaRyomenModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/sukuna.png");

    public SukunaRyomenRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SukunaRyomenModel(pContext.bakeLayer(SukunaRyomenModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(SukunaRyomenModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(SukunaRyomenModel.OUTER_LAYER)), pContext.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SukunaEntity pEntity) {
        return TEXTURE;
    }
}
