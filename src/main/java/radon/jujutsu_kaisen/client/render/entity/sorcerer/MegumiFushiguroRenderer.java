package radon.jujutsu_kaisen.client.render.entity.sorcerer;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.MegumiFushiguroModel;
import radon.jujutsu_kaisen.entity.sorcerer.MegumiFushiguroEntity;

public class MegumiFushiguroRenderer extends HumanoidMobRenderer<MegumiFushiguroEntity, MegumiFushiguroModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/megumi_fushiguro.png");

    public MegumiFushiguroRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new MegumiFushiguroModel(pContext.bakeLayer(MegumiFushiguroModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(MegumiFushiguroModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(MegumiFushiguroModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MegumiFushiguroEntity pEntity) {
        return TEXTURE;
    }
}
