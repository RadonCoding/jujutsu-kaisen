package radon.jujutsu_kaisen.pact;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class Pact {
    public Component getName() {
        ResourceLocation key = JJKPacts.getKey(this);
        return Component.translatable(String.format("pact.%s.%s", key.getNamespace(), key.getPath()));
    }

    public Component getDescription() {
        ResourceLocation key = JJKPacts.getKey(this);
        return Component.translatable(String.format("pact.%s.%s.description", key.getNamespace(), key.getPath()));
    }
}
