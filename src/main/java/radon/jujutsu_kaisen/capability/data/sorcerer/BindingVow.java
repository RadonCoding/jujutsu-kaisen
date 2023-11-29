package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum BindingVow {
    OVERTIME,
    RECOIL,
    DEATH;

    public Component getName() {
        return Component.translatable(String.format("binding_vow.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    public Component getDescription() {
        return Component.translatable(String.format("binding_vow.%s.%s.description", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
