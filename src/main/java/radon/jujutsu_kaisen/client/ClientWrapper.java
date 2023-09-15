package radon.jujutsu_kaisen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.client.gui.scren.ShadowInventoryScreen;

public class ClientWrapper {
    public static Level getLevel() {
        return Minecraft.getInstance().level;
    }

    public static void openShadowInventory() {
        Minecraft.getInstance().setScreen(new ShadowInventoryScreen());
    }

    public static void setOverlayMessage(Component component) {
        Minecraft.getInstance().gui.setOverlayMessage(component, false);
    }
}
