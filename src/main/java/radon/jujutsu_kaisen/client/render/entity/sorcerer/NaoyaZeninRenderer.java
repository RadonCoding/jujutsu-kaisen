package radon.jujutsu_kaisen.client.render.entity.sorcerer;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.NaoyaZeninModel;
import radon.jujutsu_kaisen.entity.sorcerer.NaoyaZeninEntity;

public class NaoyaZeninRenderer extends HumanoidMobRenderer<NaoyaZeninEntity, NaoyaZeninModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/naoya_zenin.png");

    public NaoyaZeninRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new NaoyaZeninModel(pContext.bakeLayer(NaoyaZeninModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(NaoyaZeninModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(NaoyaZeninModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull NaoyaZeninEntity pEntity) {
        return TEXTURE;
    }
}
