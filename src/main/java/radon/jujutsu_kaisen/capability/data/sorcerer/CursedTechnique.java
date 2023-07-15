package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;

public enum CursedTechnique {
    GETO(null),
    GOJO(JJKAbilities.UNLIMITED_VOID.get(),JJKAbilities.INFINITY.get(), JJKAbilities.RED.get(), JJKAbilities.BLUE.get(), JJKAbilities.HOLLOW_PURPLE.get(), JJKAbilities.TELEPORT.get()),
    SUKUNA(JJKAbilities.MALEVOLENT_SHRINE.get(), JJKAbilities.DISMANTLE.get(), JJKAbilities.CLEAVE.get(), JJKAbilities.FIRE_ARROW.get()),
    TOGE(null),
    YUJI(null),
    YUTA(null, JJKAbilities.RIKA.get(), JJKAbilities.COPY.get());

    private final @Nullable Ability domain;
    private final Ability[] abilities;

    CursedTechnique(@Nullable Ability domain, Ability... abilities) {
        this.domain = domain;
        this.abilities = abilities;
    }

    public @Nullable Ability getDomain() {
        return this.domain;
    }

    public Ability[] getAbilities() {
        return this.abilities;
    }

    public Component getComponent() {
        return Component.translatable(String.format("cursed_technique.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
