package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.effect.JJKPostEffects;
import radon.jujutsu_kaisen.client.effect.PostEffectHandler;
import radon.jujutsu_kaisen.client.layer.CuriosLayer;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.entity.TojiFushiguroModel;
import radon.jujutsu_kaisen.entity.sorcerer.TojiFushiguroEntity;

public class TojiFushiguroRenderer extends HumanoidMobRenderer<TojiFushiguroEntity, TojiFushiguroModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/toji_fushiguro.png");

    public TojiFushiguroRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new TojiFushiguroModel(pContext.bakeLayer(TojiFushiguroModel.LAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(TojiFushiguroModel.INNER_LAYER)),
                new HumanoidModel<>(pContext.bakeLayer(TojiFushiguroModel.OUTER_LAYER)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));
        this.addLayer(new CuriosLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TojiFushiguroEntity pEntity) {
        return TEXTURE;
    }
}
