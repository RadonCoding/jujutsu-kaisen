package radon.jujutsu_kaisen.pact;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKPacts {
    public static ResourceKey<Registry<Pact>> PACT_KEY = ResourceKey.createRegistryKey(new ResourceLocation(JujutsuKaisen.MOD_ID, "pact"));
    public static Registry<Pact> PACT_REGISTRY = new RegistryBuilder<>(PACT_KEY).sync(true).create();
    public static DeferredRegister<Pact> PACTS = DeferredRegister.create(PACT_REGISTRY, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<Pact, Pact> INVULNERABILITY = PACTS.register("invulnerability", Invulnerability::new);

    public static ResourceLocation getKey(Pact ability) {
        return PACT_REGISTRY.getKey(ability);
    }

    public static Pact getValue(ResourceLocation key) {
        return PACT_REGISTRY.get(key);
    }
}
