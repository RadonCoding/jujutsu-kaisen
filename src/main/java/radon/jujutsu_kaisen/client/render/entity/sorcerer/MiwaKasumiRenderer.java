package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.AoiTodoModel;
import radon.jujutsu_kaisen.client.model.entity.MiwaKasumiModel;
import radon.jujutsu_kaisen.entity.sorcerer.AoiTodoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.MiwaKasumiEntity;

public class MiwaKasumiRenderer extends HumanoidMobRenderer<MiwaKasumiEntity, MiwaKasumiModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/miwa_kasumi.png");

    public MiwaKasumiRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new MiwaKasumiModel(pContext.bakeLayer(MiwaKasumiModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(MiwaKasumiModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(MiwaKasumiModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MiwaKasumiEntity pEntity) {
        return TEXTURE;
    }
}
