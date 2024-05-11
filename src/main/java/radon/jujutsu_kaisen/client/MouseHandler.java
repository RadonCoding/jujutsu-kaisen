package radon.jujutsu_kaisen.client;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.MeleeMenuType;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.config.ConfigHolder;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MouseHandler {
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (ConfigHolder.CLIENT.meleeMenuType.get() != MeleeMenuType.SCROLL) return;

        double delta = event.getScrollDeltaY();

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