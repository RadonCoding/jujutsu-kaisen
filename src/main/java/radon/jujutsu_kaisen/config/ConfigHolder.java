package radon.jujutsu_kaisen.config;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec SERVER_SPEC;
    public static final ClientConfig CLIENT;
    public static final ServerConfig SERVER;

    static {
        {
            final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
            CLIENT = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
        {
            final Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
            SERVER = specPair.getLeft();
            SERVER_SPEC = specPair.getRight();
        }
    }
}
