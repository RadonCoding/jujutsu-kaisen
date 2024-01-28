package radon.jujutsu_kaisen.client.render.entity.idle_transfiguration;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.idle_transfiguration.PolymorphicSoulIsomerEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulNormalEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PolymorphicSoulIsomerRenderer extends GeoEntityRenderer<PolymorphicSoulIsomerEntity> {
    public PolymorphicSoulIsomerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "polymorphic_soul_isomer")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PolymorphicSoulIsomerEntity animatable) {
        ResourceLocation key = super.getTextureLocation(animatable);
        return new ResourceLocation(key.getNamespace(), key.getPath().replace(".png",
                String.format("_%s.%s", animatable.getVariant().ordinal() + 1, "png")));
    }
}
