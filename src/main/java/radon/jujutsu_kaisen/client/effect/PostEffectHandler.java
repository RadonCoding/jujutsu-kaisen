package radon.jujutsu_kaisen.client.effect;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.effect.base.PostEffect;

// Credit: https://github.com/M-Marvin/MCMOD-HoloStructures-V2/blob/main-1.20.4/HoloStructures-1.20/src/main/java/de/m_marvin/holostruct/client/rendering/posteffect/PostEffectUtil.java
@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PostEffectHandler {
    private static final int BUFFER_BUILDER_CAPACITY = 786432;
    private static final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(BUFFER_BUILDER_CAPACITY));

    public static MultiBufferSource.BufferSource bufferSource() {
        return bufferSource;
    }

    public static void preparePostEffect(PostChain chain) {
        chain.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    public static void clearFramebuffer(RenderTarget target) {
        target.bindWrite(true);
        target.clear(Minecraft.ON_OSX);
    }

    public static void bindFramebuffer(RenderTarget target) {
        target.bindWrite(true);
    }

    public static void unbindFramebuffer(RenderTarget target) {
        target.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            for (PostEffect effect : JJKPostEffects.EFFECTS) {
                RenderTarget target = effect.getCustomTarget();

                if (target == null) continue;

                preparePostEffect(effect.getChain());
                clearFramebuffer(target);
                unbindFramebuffer(target);
            }
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            for (PostEffect effect : JJKPostEffects.EFFECTS) {
               if (!effect.shouldRender()) continue;

               effect.getChain().process(event.getPartialTick());

               Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
            }
        }
    }
}
