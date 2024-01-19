package radon.jujutsu_kaisen.config;

import net.minecraftforge.common.ForgeConfigSpec;
import radon.jujutsu_kaisen.client.gui.MeleeMenuType;

public class ClientConfig {
    public final ForgeConfigSpec.EnumValue<MeleeMenuType> meleeMenuType;
    public final ForgeConfigSpec.BooleanValue visibleCursedEnergy;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Settings").push("settings");
        this.meleeMenuType = builder.comment("The melee menu type")
                .defineEnum("meleeMenuType", MeleeMenuType.TOGGLE);
        this.visibleCursedEnergy = builder.comment("Whether or not cursed energy particles are visible")
                        .define("visibleCursedEnergy", true);
        builder.pop();
    }
}
