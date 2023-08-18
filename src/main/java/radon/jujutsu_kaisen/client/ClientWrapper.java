package radon.jujutsu_kaisen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientWrapper {
    public static Level getLevel() {
        return Minecraft.getInstance().level;
    }
}
