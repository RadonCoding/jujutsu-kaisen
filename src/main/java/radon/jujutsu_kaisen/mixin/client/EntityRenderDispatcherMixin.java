package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.client.JJKRenderers;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
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
}
