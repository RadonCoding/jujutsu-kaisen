package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ScreenFlashOverlay {
    private static final int DEFAULT_DURATION = 20;
    private static FlashEvent current;

    @SubscribeEvent
    public static void onClientTickPre(ClientTickEvent.Pre event) {
        if (current == null) return;

        current.duration--;

        if (current.duration <= 0) {
            current = null;
        }
    }

    public static void flash() {
        current = new FlashEvent(DEFAULT_DURATION);
    }

    public static LayeredDraw.Layer OVERLAY = (pGuiGraphics, pPartialTick) -> {
        if (current == null) return;

        Minecraft mc = Minecraft.getInstance();

        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        float alpha = (float) current.duration / DEFAULT_DURATION;
        pGuiGraphics.fill(0, 0, width, height, HelperMethods.toRGB24(255, 255, 255, (int) (255 * alpha)));
    };

    public static class FlashEvent {
        public int duration;

        public FlashEvent(int duration) {
            this.duration = duration;
        }
    }
}