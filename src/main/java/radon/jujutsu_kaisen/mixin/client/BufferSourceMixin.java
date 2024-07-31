package radon.jujutsu_kaisen.mixin.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.client.VertexCapturer;
import radon.jujutsu_kaisen.client.slice.RigidBody;

import java.util.List;

@Mixin(MultiBufferSource.BufferSource.class)
public class BufferSourceMixin {
    @Inject(method = "endBatch(Lnet/minecraft/client/renderer/RenderType;)V", at = @At(value = "TAIL", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/RenderType;)V"))
    public void endBatch(RenderType type, CallbackInfo ci) {
        if (!VertexCapturer.capture) return;

        VertexCapturer.captured.add(new VertexCapturer.Capture(RenderSystem.getShaderTexture(0), type,
                ImmutableList.copyOf(VertexCapturer.currentTriangles)));

        VertexCapturer.currentTriangles.clear();
    }
}
