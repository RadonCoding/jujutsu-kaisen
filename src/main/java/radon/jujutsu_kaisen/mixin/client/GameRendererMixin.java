package radon.jujutsu_kaisen.mixin.client;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.client.effect.JJKPostEffects;
import radon.jujutsu_kaisen.client.effect.base.PostEffect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V", shift = At.Shift.BEFORE))
    private void afterRenderPostEffects(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            Window window = mc.getWindow();

            for (PostEffect effect : JJKPostEffects.EFFECTS) {
                if (effect.shouldRender(mc.player)) {
                    effect.resize(window.getWidth(), window.getHeight());
                    effect.render(mc.getFrameTime());
                }
            }
        }
    }
}