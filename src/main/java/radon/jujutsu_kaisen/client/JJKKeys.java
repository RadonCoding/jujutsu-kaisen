package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKKeys {
    public static String KEY_CATEGORY_JUJUTSU_KAISEN = String.format("key.category.%s", JujutsuKaisen.MOD_ID);
    public static KeyMapping ABILITY_SCROLL = createKeyMapping("ability_scroll",
            InputConstants.KEY_LALT);
    public static KeyMapping ACTIVATE_ABILITY = createKeyMapping("activate_ability",
            InputConstants.KEY_R);
    public static KeyMapping ABILITY_UP = createKeyMapping("ability_up",
            -1);
    public static KeyMapping ABILITY_DOWN = createKeyMapping("ability_down",
            -1);
    public static KeyMapping ACTIVATE_RCT_OR_HEAL = createKeyMapping("activate_rct_or_heal",
            InputConstants.KEY_X);
    public static KeyMapping OPEN_INVENTORY_CURSE = createKeyMapping("open_inventory_curse",
            InputConstants.KEY_B);
    public static KeyMapping ACTIVATE_WATER_WALKING = createKeyMapping("activate_water_walking",
            InputConstants.KEY_N);
    public static KeyMapping ACTIVATE_CURSED_ENERGY_SHIELD = createKeyMapping("activate_cursed_energy_shield",
            InputConstants.KEY_Z);
    public static KeyMapping SHOW_ABILITY_MENU = createKeyMapping("show_ability_menu",
            InputConstants.KEY_C);
    public static KeyMapping SHOW_DOMAIN_MENU = createKeyMapping("show_domain_menu",
            InputConstants.KEY_V);
    public static KeyMapping DASH = createKeyMapping("dash",
            InputConstants.KEY_G);
    public static KeyMapping OPEN_JUJUTSU_MENU = createKeyMapping("open_jujutsu_menu",
            InputConstants.KEY_P);
    public static KeyMapping INCREASE_OUTPUT = createKeyMapping("increase_output",
            InputConstants.KEY_UP);
    public static KeyMapping DECREASE_OUTPUT = createKeyMapping("decrease_output",
            InputConstants.KEY_DOWN);

    private static KeyMapping createKeyMapping(String name, int keyCode) {
        return new KeyMapping(String.format("key.%s.%s", JujutsuKaisen.MOD_ID, name), keyCode, KEY_CATEGORY_JUJUTSU_KAISEN);
    }
}
