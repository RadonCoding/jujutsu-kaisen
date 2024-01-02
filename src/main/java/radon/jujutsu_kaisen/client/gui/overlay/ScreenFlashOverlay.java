package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ScreenFlashOverlay {
    private static final int DEFAULT_DURATION = 20;
    private static FlashEvent current;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;

        if (current == null) return;

        current.duration--;

        if (current.duration <= 0) {
            current = null;
        }
    }

    public static void flash() {
        current = new FlashEvent(DEFAULT_DURATION);
    }

    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        if (current == null) return;

        float alpha = (float) current.duration / DEFAULT_DURATION;
        graphics.fill(0, 0, width, height, HelperMethods.toRGB24(255, 255, 255, (int) (255 * alpha)));
    };

    public static class FlashEvent {
        public int duration;

        public FlashEvent(int duration) {
            this.duration = duration;
        }
    }
}