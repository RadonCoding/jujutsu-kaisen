package radon.jujutsu_kaisen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.client.gui.scren.CurseSummonScreen;
import radon.jujutsu_kaisen.client.gui.scren.ShadowInventoryScreen;

public class ClientWrapper {
    public static Level getLevel() {
        return Minecraft.getInstance().level;
    }

    public static void openShadowInventory() {
        Minecraft.getInstance().setScreen(new ShadowInventoryScreen());
    }

    public static void openCurseMenu() {
        Minecraft.getInstance().setScreen(new CurseSummonScreen());
    }
}
