package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogEntity;

public class DivineDogRenderer extends TenShadowsRenderer<DivineDogEntity> {
    public DivineDogRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "divine_dog")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DivineDogEntity animatable) {
        ResourceLocation key = super.getTextureLocation(animatable);
        return new ResourceLocation(key.getNamespace(), key.getPath().replace(".png",
                String.format("_%s.%s", animatable.getVariant().name().toLowerCase(), "png")));
    }
}
