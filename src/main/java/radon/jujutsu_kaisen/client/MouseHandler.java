package radon.jujutsu_kaisen.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.config.ConfigHolder;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MouseHandler {
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (ConfigHolder.CLIENT.meleeMenuType.get() != 1) return;

        double delta = event.getScrollDelta();

        int i = (int) Math.signum(delta);

        if (i == 0) {
            return;
        }

        if (JJKKeys.ACTIVATE_MELEE_MENU.isDown()) {
            if (AbilityOverlay.scroll(i)) {
                event.setCanceled(true);
            }
        }
    }
}