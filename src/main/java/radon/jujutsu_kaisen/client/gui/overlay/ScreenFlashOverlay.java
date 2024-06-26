package radon.jujutsu_kaisen.client.gui.overlay;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.util.HelperMethods;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ScreenFlashOverlay {
    private static final int DEFAULT_DURATION = 20;
    private static FlashEvent current;
    public static LayeredDraw.Layer OVERLAY = (pGuiGraphics, pPartialTick) -> {
        if (current == null) return;

        Minecraft mc = Minecraft.getInstance();

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        float alpha = (float) current.duration / DEFAULT_DURATION;
        pGuiGraphics.fill(0, 0, width, height, HelperMethods.toRGB24(255, 255, 255, (int) (255 * alpha)));
    };

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

    public static class FlashEvent {
        public int duration;

        public FlashEvent(int duration) {
            this.duration = duration;
        }
    }
}