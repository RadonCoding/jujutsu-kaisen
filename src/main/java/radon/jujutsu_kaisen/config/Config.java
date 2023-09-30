package radon.jujutsu_kaisen.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import radon.jujutsu_kaisen.entity.JJKEntities;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final Map<ResourceLocation, Float> AMOUNTS = new HashMap<>();

    static {
        AMOUNTS.put(JJKEntities.RIKA.getId(), Float.POSITIVE_INFINITY);
        AMOUNTS.put(JJKEntities.MAHORAGA.getId(), 250000.0F);

        AMOUNTS.put(JJKEntities.JOGO.getId(), 4000.0F);
        AMOUNTS.put(JJKEntities.DAGON.getId(), 4000.0F);
        AMOUNTS.put(JJKEntities.HANAMI.getId(), 4000.0F);

        AMOUNTS.put(JJKEntities.SUKUNA_RYOMEN.getId(), 10000.0F);
        AMOUNTS.put(JJKEntities.MEGUNA_RYOMEN.getId(), 10000.0F);
        AMOUNTS.put(JJKEntities.SATORU_GOJO.getId(), 5000.0F);
        AMOUNTS.put(JJKEntities.YUTA_OKKOTSU.getId(), 7500.0F);
        AMOUNTS.put(JJKEntities.HEIAN_SUKUNA.getId(), 10000.0F);
    }

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> maxCursedEnergyNPC = BUILDER.comment("Maximum cursed energy for NPCs")
            .defineList("maxCursedEnergyNPC", AMOUNTS
                    .entrySet()
                    .stream()
                    .map(x -> String.format(Locale.ROOT, "%s=%f", x.getKey().toString(), x.getValue()))
                    .toList(), obj -> obj instanceof String);
    public static final ForgeConfigSpec.ConfigValue<Float> maxCursedEnergyDefault = BUILDER.comment("Maximum default cursed energy")
            .define("maxCursedEnergyDefault", 2500.0F);;


    public static Map<ResourceLocation, Float> getMaxCursedEnergyNPC() {
        Map<ResourceLocation, Float> amounts = new HashMap<>();

        for (String line : maxCursedEnergyNPC.get()) {
            String[] parts = line.split("=");
            String key = parts[0];
            float value = Float.parseFloat(parts[1]);
            amounts.put(new ResourceLocation(key), value);
        }
        return amounts;
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}