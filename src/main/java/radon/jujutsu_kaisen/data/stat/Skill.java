package radon.jujutsu_kaisen.data.stat;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

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
    REGENERATION,
    STRENGTH,
    SHIELDING;

    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
            return !this.isJujutsu();
        } else {
            return !this.isPhysical();
        }
    }

    public boolean isJujutsu() {
        return this == Skill.BARRIER || this == Skill.OUTPUT || this == Skill.REINFORCEMENT || this == Skill.ENERGY || this == Skill.REGENERATION;
    }

    public boolean isPhysical() {
        return this == Skill.STRENGTH || this == Skill.SHIELDING;
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
