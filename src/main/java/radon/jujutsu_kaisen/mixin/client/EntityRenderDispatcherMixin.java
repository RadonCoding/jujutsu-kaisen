package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.client.JJKRenderers;
import radon.jujutsu_kaisen.client.MixinData;
import radon.jujutsu_kaisen.effect.JJKEffects;
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

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void shouldRender(E pEntity, Frustum pFrustum, double pCamX, double pCamY, double pCamZ, CallbackInfoReturnable<Boolean> cir) {
        if (!MixinData.isFakeRender) {
            if (pEntity instanceof LivingEntity living && living.hasEffect(JJKEffects.INVISIBILITY.get())) {
                cir.setReturnValue(false);
                return;
            }
            for (ProjectionFrameEntity frame : pEntity.level().getEntitiesOfClass(ProjectionFrameEntity.class, AABB.ofSize(pEntity.position(),
                    8.0D, 8.0D, 8.0D))) {
                if (frame.getVictim() == pEntity) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
