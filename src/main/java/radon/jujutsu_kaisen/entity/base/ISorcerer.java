package radon.jujutsu_kaisen.entity.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.List;
import java.util.Map;

public interface ISorcerer {
    boolean canPerformSorcery();
    SorcererGrade getGrade();
    @Nullable CursedTechnique getTechnique();
    default @Nullable CursedTechnique getAdditional() {
        return null;
    }
    default @NotNull List<Trait> getTraits() { return List.of(); }
    default @NotNull List<Ability> getCustom() { return List.of(); }
    default List<Ability> getUnlocked() { return List.of(); }
    JujutsuType getJujutsuType();
    default float getExperienceMultiplier() {
        return 0.0F;
    }

    @Nullable Ability getDomain();

    default void init(ISorcererData data) {
        data.setGrade(this.getGrade());
        data.setTechnique(this.getTechnique());
        data.setAdditional(this.getAdditional());
        data.addTraits(this.getTraits());
        data.setType(this.getJujutsuType());
        data.unlockAll(this.getUnlocked());

        Map<ResourceLocation, Float> energy = ConfigHolder.SERVER.getCursedEnergyAmounts();
        Map<ResourceLocation, Float> experience = ConfigHolder.SERVER.getExperienceMultipliers();
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(((Entity) this).getType());

        if (energy.containsKey(key)) {
            data.setMaxEnergy(energy.get(key));
        }
        data.setEnergy(data.getMaxEnergy((LivingEntity) this));

        if (experience.containsKey(key)) {
            data.setExperience(data.getExperience() * experience.get(key));
        } else if (this.getExperienceMultiplier() > 0.0F) {
            data.setExperience(data.getExperience() * this.getExperienceMultiplier());
        }
    }
}
