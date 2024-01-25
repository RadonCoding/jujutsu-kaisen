package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.TenShadowsRenderer;
import radon.jujutsu_kaisen.entity.TransfiguredSoulSmallEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TransfiguredSoulSmallRenderer extends GeoEntityRenderer<TransfiguredSoulSmallEntity> {
    public TransfiguredSoulSmallRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "transfigured_soul_small")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TransfiguredSoulSmallEntity animatable) {
        ResourceLocation key = super.getTextureLocation(animatable);
        return new ResourceLocation(key.getNamespace(), key.getPath().replace(".png",
                String.format("_%s.%s", animatable.getVariant().ordinal() + 1, "png")));
    }
}
