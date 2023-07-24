package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.GojoSatoruModel;
import radon.jujutsu_kaisen.entity.sorcerer.SaturoGojoEntity;

public class SatoruGojoRenderer extends HumanoidMobRenderer<SaturoGojoEntity, GojoSatoruModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/satoru_satoru.png");

    public SatoruGojoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new GojoSatoruModel(pContext.bakeLayer(GojoSatoruModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(GojoSatoruModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(GojoSatoruModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SaturoGojoEntity pEntity) {
        return TEXTURE;
    }
}
