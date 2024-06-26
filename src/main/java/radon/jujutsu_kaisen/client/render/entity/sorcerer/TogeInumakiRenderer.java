package radon.jujutsu_kaisen.client.render.entity.sorcerer;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.TogeInumakiModel;
import radon.jujutsu_kaisen.entity.sorcerer.TogeInumakiEntity;

public class TogeInumakiRenderer extends HumanoidMobRenderer<TogeInumakiEntity, TogeInumakiModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/toge_inumaki.png");

    public TogeInumakiRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new TogeInumakiModel(pContext.bakeLayer(TogeInumakiModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(TogeInumakiModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(TogeInumakiModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TogeInumakiEntity pEntity) {
        return TEXTURE;
    }
}
