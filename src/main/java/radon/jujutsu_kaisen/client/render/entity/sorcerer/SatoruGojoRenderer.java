package radon.jujutsu_kaisen.client.render.entity.sorcerer;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.SatoruGojoModel;
import radon.jujutsu_kaisen.entity.sorcerer.SatoruGojoEntity;

public class SatoruGojoRenderer extends HumanoidMobRenderer<SatoruGojoEntity, SatoruGojoModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/satoru_gojo.png");

    public SatoruGojoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SatoruGojoModel(pContext.bakeLayer(SatoruGojoModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(SatoruGojoModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(SatoruGojoModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SatoruGojoEntity pEntity) {
        return TEXTURE;
    }
}
