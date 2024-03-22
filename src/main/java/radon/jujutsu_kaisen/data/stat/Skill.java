package radon.jujutsu_kaisen.data.stat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;

public enum Skill {
    SOUL,
    BARRIER,
    OUTPUT,
    REINFORCEMENT,
    ENERGY,
    REGENERATION;

    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        if (data.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            return !this.isJujutsu();
        }
        return true;
    }

    public boolean isJujutsu() {
        return this == Skill.BARRIER || this == Skill.ENERGY || this == Skill.REGENERATION;
    }

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
