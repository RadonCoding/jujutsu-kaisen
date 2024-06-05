package radon.jujutsu_kaisen.config;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.neoforged.neoforge.common.ModConfigSpec;
import radon.jujutsu_kaisen.client.gui.MeleeMenuType;

public class ClientConfig {
    public final ModConfigSpec.EnumValue<MeleeMenuType> meleeMenuType;
    public final ModConfigSpec.BooleanValue visibleCursedEnergy;

    public ClientConfig(ModConfigSpec.Builder builder) {
        builder.comment("Settings").push("settings");
        this.meleeMenuType = builder.comment("The melee menu type")
                .defineEnum("meleeMenuType", MeleeMenuType.TOGGLE);
        this.visibleCursedEnergy = builder.comment("Whether or not cursed energy particles are visible")
                        .define("visibleCursedEnergy", true);
        builder.pop();
    }
}
