package radon.jujutsu_kaisen.client.post;


import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Credit: https://github.com/M-Marvin/MCMOD-HoloStructures-V2/blob/main-1.20.4/HoloStructures-1.20/src/main/java/de/m_marvin/holostruct/client/rendering/posteffect/PostEffectUtil.java
@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class PostEffectHandler {
    private static final List<PostEffect> active = new ArrayList<>();

    public static void bind(PostEffect effect) {
        Minecraft mc = Minecraft.getInstance();

        RenderTarget target = effect.getCustomTarget();

        target.copyDepthFrom(mc.getMainRenderTarget());

        Window window = mc.getWindow();
        effect.getPostChain().resize(window.getWidth(), window.getHeight());

        target.bindWrite(false);

        active.add(effect);
    }

    public static void unbind(PostEffect effect) {
        Minecraft mc = Minecraft.getInstance();

        RenderTarget target = effect.getCustomTarget();

        target.unbindWrite();
        mc.getMainRenderTarget().bindWrite(false);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            Iterator<PostEffect> iter = active.iterator();

            while (iter.hasNext()) {
                PostEffect effect = iter.next();
                effect.getPostChain().process(event.getPartialTick());
                mc.getMainRenderTarget().bindWrite(false);
                iter.remove();
            }
        }
    }
}