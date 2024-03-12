package radon.jujutsu_kaisen.data.stat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum Skill {
    SOUL,
    BARRIER,
    OUTPUT,
    REINFORCEMENT,
    ENERGY,
    REGENERATION;

    public Component getName() {
        return Component.translatable(String.format("skill.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    public Component getDescription() {
        return Component.translatable(String.format("skill.%s.%s.description", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(JujutsuKaisen.MOD_ID, String.format("textures/skill/%s.png", this.name().toLowerCase()));
    }
}
