package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKKeys {
    public static String KEY_CATEGORY_JUJUTSU_KAISEN = String.format("key.category.%s", JujutsuKaisen.MOD_ID);
    public static KeyMapping ABILITY_SCROLL  = createKeyMapping("ability_scroll",
            InputConstants.KEY_LALT);
    public static KeyMapping ACTIVATE_ABILITY  = createKeyMapping("activate_ability",
            InputConstants.KEY_R);
    public static KeyMapping ABILITY_RIGHT = createKeyMapping("ability_right",
            -1);
    public static KeyMapping ABILITY_LEFT = createKeyMapping("ability_left",
            -1);
    public static KeyMapping ACTIVATE_RCT_OR_HEAL = createKeyMapping("activate_rct_or_heal",
            InputConstants.KEY_X);
    public static KeyMapping OPEN_INVENTORY_CURSE = createKeyMapping("open_inventory_curse",
            InputConstants.KEY_B);
    public static KeyMapping ACTIVATE_WATER_WALKING = createKeyMapping("activate_water_walking",
            InputConstants.KEY_Z);
    public static KeyMapping SHOW_ABILITY_MENU = createKeyMapping("show_ability_menu",
            InputConstants.KEY_C);
    public static KeyMapping SHOW_DOMAIN_MENU = createKeyMapping("show_domain_menu",
            InputConstants.KEY_V);
    public static KeyMapping DASH = createKeyMapping("dash",
            InputConstants.KEY_G);

    private static KeyMapping createKeyMapping(String name, int keyCode) {
        return new KeyMapping(String.format("key.%s.%s", JujutsuKaisen.MOD_ID, name), keyCode, KEY_CATEGORY_JUJUTSU_KAISEN);
    }
}
