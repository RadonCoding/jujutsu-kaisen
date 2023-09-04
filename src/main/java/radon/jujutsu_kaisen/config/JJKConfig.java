package radon.jujutsu_kaisen.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import radon.jujutsu_kaisen.entity.JJKEntities;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JJKConfig {
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> maxCursedEnergyNPC;
    public final ForgeConfigSpec.ConfigValue<Float> maxCursedEnergyDefault;

    public JJKConfig(ForgeConfigSpec.Builder builder) {
        builder.push("General");

        this.maxCursedEnergyDefault = builder
                .comment("Maximum default cursed energy")
                        .define("maxCursedEnergyDefault", 2500.0F);

        builder.pop();

        builder.push("NPC");

        Map<ResourceLocation, Float> amounts = new HashMap<>();
        amounts.put(JJKEntities.RIKA.getId(), Float.POSITIVE_INFINITY);
        amounts.put(JJKEntities.MAHORAGA.getId(), 250000.0F);
        amounts.put(JJKEntities.JOGO.getId(), 4000.0F);
        amounts.put(JJKEntities.DAGON.getId(), 4000.0F);
        amounts.put(JJKEntities.SUKUNA_RYOMEN.getId(), 10000.0F);
        amounts.put(JJKEntities.MEGUNA_RYOMEN.getId(), 10000.0F);
        amounts.put(JJKEntities.SATORU_GOJO.getId(), 5000.0F);
        amounts.put(JJKEntities.YUTA_OKKOTSU.getId(), 7500.0F);

        this.maxCursedEnergyNPC = builder
                .comment("Maximum cursed energy for NPCs")
                .defineList("maxCursedEnergyNPC", amounts
                        .entrySet()
                        .stream()
                        .map(x -> String.format(Locale.ROOT, "%s=%f", x.getKey().toString(), x.getValue()))
                        .toList(), obj -> obj instanceof String);

        builder.pop();
    }

    public Map<ResourceLocation, Float> getMaxCursedEnergyNPC() {
        Map<ResourceLocation, Float> amounts = new HashMap<>();

        for (String line : this.maxCursedEnergyNPC.get()) {
            String[] parts = line.split("=");
            String key = parts[0];
            float value = Float.parseFloat(parts[1]);
            amounts.put(new ResourceLocation(key), value);
        }
        return amounts;
    }
}
