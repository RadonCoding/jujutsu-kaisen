package radon.jujutsu_kaisen.mixin.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(GeoEntityRenderer.class)
public interface IGeoEntityRendererAccessor<T extends Entity & GeoAnimatable> {
    @Invoker
    void invokeApplyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
                              float partialTick);
}
