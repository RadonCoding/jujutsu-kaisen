package radon.jujutsu_kaisen.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public final ForgeConfigSpec.IntValue meleeMenuType;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Settings").push("settings");
        this.meleeMenuType = builder.comment("The melee menu type (1 = scroll, 2 = toggled)")
                .defineInRange("meleeMenuType", 2, 1, 2);
        builder.pop();
    }
}
