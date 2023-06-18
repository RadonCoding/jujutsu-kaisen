package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.SpecialTrait;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.concurrent.atomic.AtomicBoolean;

public class CurseRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {
    public CurseRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    @Override
    public boolean shouldRender(@NotNull T pLivingEntity, @NotNull Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        AtomicBoolean result = new AtomicBoolean(true);

        mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getTrait() == SpecialTrait.HEAVENLY_RESTRICTION) {
                result.set(false);
            }
        });

        if (result.get()) {
            return super.shouldRender(pLivingEntity, pCamera, pCamX, pCamY, pCamZ);
        }
        return false;
    }
}
