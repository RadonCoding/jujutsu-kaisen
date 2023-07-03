package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.concurrent.atomic.AtomicReference;

public class CurseRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {
    public CurseRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        AtomicReference<RenderType> result = new AtomicReference<>(super.getRenderType(animatable, texture, bufferSource, partialTick));

        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getTrait() == Trait.HEAVENLY_RESTRICTION) {
                result.set(RenderType.entityTranslucent(texture));
            }
        });
        return result.get();
    }

    @Override
    public Color getRenderColor(T animatable, float partialTick, int packedLight) {
        AtomicReference<Color> result = new AtomicReference<>(super.getRenderColor(animatable, partialTick, packedLight));

        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getTrait() == Trait.HEAVENLY_RESTRICTION) {
                result.set(Color.ofRGBA(1.0F, 1.0F, 1.0F, 0.1F));
            }
        });
        return result.get();
    }
}
