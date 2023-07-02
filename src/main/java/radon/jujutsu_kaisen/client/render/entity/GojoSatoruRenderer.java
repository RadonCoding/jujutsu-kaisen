package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.GojoSatoruModel;
import radon.jujutsu_kaisen.entity.GojoSatoruEntity;

public class GojoSatoruRenderer extends HumanoidMobRenderer<GojoSatoruEntity, GojoSatoruModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/gojo_satoru.png");

    public GojoSatoruRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new GojoSatoruModel(pContext.bakeLayer(GojoSatoruModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new GojoSatoruModel(pContext.bakeLayer(GojoSatoruModel.INNER_LAYER)),
                new GojoSatoruModel(pContext.bakeLayer(GojoSatoruModel.OUTER_LAYER))));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GojoSatoruEntity pEntity) {
        return TEXTURE;
    }
}
