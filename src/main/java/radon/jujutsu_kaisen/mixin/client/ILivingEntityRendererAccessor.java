package radon.jujutsu_kaisen.mixin.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface ILivingEntityRendererAccessor {
    @Invoker
    void invokeSetupRotations(LivingEntity pEntity, PoseStack pPoseStack, float pBob, float pYBodyRot, float pPartialTick, float pScale);

    @Invoker
    void invokeScale(LivingEntity pLivingEntity, PoseStack pPoseStack, float pPartialTickTime);

    @Invoker
    float invokeGetAttackAnim(LivingEntity pLivingBase, float pPartialTickTime);

    @Invoker
    float invokeGetBob(LivingEntity pLivingBase, float pPartialTick);
}
