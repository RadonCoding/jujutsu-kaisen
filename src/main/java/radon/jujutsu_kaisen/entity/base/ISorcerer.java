package radon.jujutsu_kaisen.entity.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.List;
import java.util.Map;

public interface ISorcerer {
    SorcererGrade getGrade();
    @Nullable CursedTechnique getTechnique();
    default @Nullable CursedTechnique getAdditional() {
        return null;
    }
    default @NotNull List<Trait> getTraits() { return List.of(); }
    default @NotNull List<Ability> getCustom() { return List.of(); }
    JujutsuType getJujutsuType();

    @Nullable Ability getDomain();

    default void init(ISorcererData data) {
        data.setGrade(this.getGrade());
        data.setTechnique(this.getTechnique());
        data.setAdditional(this.getAdditional());
        data.addTraits(this.getTraits());
        data.setType(this.getJujutsuType());

        Map<ResourceLocation, Float> config = ConfigHolder.SERVER.getMaxCursedEnergyNPC();
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(((Entity) this).getType());

        if (config.containsKey(key)) {
            data.setMaxEnergy(config.get(key));
        }
        data.setEnergy(data.getMaxEnergy());
    }
}
