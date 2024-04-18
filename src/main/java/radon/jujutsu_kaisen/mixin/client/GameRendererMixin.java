package radon.jujutsu_kaisen.mixin.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

        if (mc.player == null) return;

        Window window = mc.getWindow();

        for (PostEffect effect : JJKPostEffects.EFFECTS) {
            if (!effect.shouldRender(mc.player)) continue;

            effect.resize(window.getWidth(), window.getHeight());
            effect.render(mc.getFrameTime());
        }
    }
}