package radon.jujutsu_kaisen.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class ConfigHolder {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final JJKConfig CLIENT;
    public static final JJKConfig SERVER;

    static {
        {
            final Pair<JJKConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(JJKConfig::new);
            CLIENT = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
        {
            final Pair<JJKConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(JJKConfig::new);
            SERVER = specPair.getLeft();
            SERVER_SPEC = specPair.getRight();
        }
    }
}
