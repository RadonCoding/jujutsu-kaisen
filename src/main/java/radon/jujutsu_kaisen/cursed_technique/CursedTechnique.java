package radon.jujutsu_kaisen.cursed_technique;


import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CursedTechnique {
    private final Set<Holder<Ability>> abilities;

    @Nullable
    private final Holder<Ability> domain;

    public CursedTechnique(Set<Holder<Ability>> abilities, @Nullable Holder<Ability> domain) {
        this.abilities = abilities;
        this.domain = domain;
    }

    public Set<Ability> getAbilities() {
        Set<Ability> abilities = new LinkedHashSet<>();

        for (Holder<Ability> holder : this.abilities) {
            abilities.add(holder.value());
        }
        return abilities;
    }

    @Nullable
    public Ability getDomain() {
        return this.domain == null ? null : this.domain.value();
    }

    public Component getName() {
        ResourceLocation key = JJKCursedTechniques.getKey(this);
        return Component.translatable(String.format("cursed_technique.%s.%s", key.getNamespace(), key.getPath()));
    }

    public static class Builder {
        private final LinkedHashSet<Holder<Ability>> abilities = new LinkedHashSet<>();

        @Nullable
        private Holder<Ability> domain;

        @SafeVarargs
        public final Builder abilities(Holder<Ability>... abilities) {
            this.abilities.addAll(List.of(abilities));
            return this;
        }

        public Builder domain(Holder<Ability> ability) {
            this.domain = ability;
            return this;
        }

        public CursedTechnique build() {
            return new CursedTechnique(this.abilities, this.domain);
        }
    }
}
