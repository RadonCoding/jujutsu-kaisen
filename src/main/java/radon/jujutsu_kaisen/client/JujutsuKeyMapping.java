package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JujutsuKeyMapping {
    public static String KEY_CATEGORY_JUJUTSU_KAISEN = String.format("key.category.%s", JujutsuKaisen.MOD_ID);
    public static KeyMapping KEY_ACTIVATE_ABILITY = createKeyMapping("activate",
            InputConstants.KEY_R);

    private static KeyMapping createKeyMapping(String name, int keyCode) {
        return new KeyMapping(String.format("key.%s.%s", JujutsuKaisen.MOD_ID, name), keyCode, KEY_CATEGORY_JUJUTSU_KAISEN);
    }
}
