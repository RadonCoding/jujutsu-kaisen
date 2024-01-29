package radon.jujutsu_kaisen.capability.data.ten_shadows;

import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum TenShadowsMode {
    SUMMON,
    ABILITY;

    public Component getName() {
        return Component.translatable(String.format("ten_shadows_mode.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
