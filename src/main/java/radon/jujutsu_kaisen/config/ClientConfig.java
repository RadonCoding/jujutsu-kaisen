package radon.jujutsu_kaisen.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public ClientConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Client configuration settings")
                .push("client");

        builder.pop();
    }
}
