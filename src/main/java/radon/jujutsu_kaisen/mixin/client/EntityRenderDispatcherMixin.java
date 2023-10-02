package radon.jujutsu_kaisen.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.client.ability.ClientProjectionHandler;
import radon.jujutsu_kaisen.client.JJKRenderers;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import radon.jujutsu_kaisen.entity.effect.ProjectionFrameEntity;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin<E extends Entity> {
    @ModifyVariable(method = "onResourceManagerReload", at = @At("TAIL"))
    public EntityRendererProvider.Context onResourceManagerReload(EntityRendererProvider.Context pContext) {
        JJKRenderers.bake(pContext);
        return pContext;
    }

    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    public void getRenderer(Entity pEntity, CallbackInfoReturnable<EntityRenderer<?>> cir) {
        if (pEntity instanceof JJKPartEntity<?> part) {
            cir.setReturnValue(JJKRenderers.lookup(part.getRenderer()));
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    public void render(EntityRenderer<E> instance, E pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        instance.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);

        ClientVisualHandler.VisualData data = ClientVisualHandler.getOrRequest(pEntity);

        if (data == null || data.speedStacks() == 0) return;

        double deltaX = pEntity.xOld - pEntity.getX();
        double deltaY = pEntity.yOld - pEntity.getY();
        double deltaZ = pEntity.zOld - pEntity.getZ();

        double delta = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        int number = (int) (delta / 0.5D);

        for (int i = 0; i < number; i++) {
            pPoseStack.pushPose();

            double offsetX = deltaX * (i + 1) / number;
            double offsetY = deltaY * (i + 1) / number;
            double offsetZ = deltaZ * (i + 1) / number;

            pPoseStack.translate(offsetX, offsetY, offsetZ);

            ClientProjectionHandler.alpha = 0.5F * (1.0F - ((float) i / number));
            ClientProjectionHandler.anim = 1.0F - ((float) i / number);
            ClientProjectionHandler.mirage = true;
            instance.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
            ClientProjectionHandler.mirage = false;
            pPoseStack.popPose();
        }
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void shouldRender(E pEntity, Frustum pFrustum, double pCamX, double pCamY, double pCamZ, CallbackInfoReturnable<Boolean> cir) {
        if (!ClientProjectionHandler.frame) {
            for (ProjectionFrameEntity frame : pEntity.level().getEntitiesOfClass(ProjectionFrameEntity.class, AABB.ofSize(pEntity.position(),
                    8.0D, 8.0D, 8.0D))) {
                if (frame.getVictim() == pEntity) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
