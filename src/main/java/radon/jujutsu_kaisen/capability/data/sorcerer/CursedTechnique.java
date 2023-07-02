package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.CurseEntity;

import java.util.ArrayList;
import java.util.List;

public enum CursedTechnique {
    NONE,
    GETO,
    GOJO(JJKAbilities.INFINITY.get(), JJKAbilities.RED.get(), JJKAbilities.BLUE.get(), JJKAbilities.HOLLOW_PURPLE.get(), JJKAbilities.UNLIMITED_VOID.get()),
    SUKUNA(JJKAbilities.DISMANTLE.get(), JJKAbilities.CLEAVE.get(), JJKAbilities.MALEVOLENT_SHRINE.get()),
    TOGE,
    YUJI,
    YUTA;

    private final Ability[] abilities;

    CursedTechnique(Ability... abilities) {
        this.abilities = abilities;
    }

    public Ability[] getAbilities(LivingEntity owner) {
        List<Ability> abilities = new ArrayList<>();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getTrait() == Trait.HEAVENLY_RESTRICTION) {
                abilities.add(JJKAbilities.DASH.get());
            } else {
                abilities.add(JJKAbilities.SMASH.get());
                abilities.add(JJKAbilities.RCT.get());
            }
        });

        if (owner instanceof CurseEntity) {
            abilities.add(JJKAbilities.HEAL.get());
        }

        Ability[] result = new Ability[abilities.size() + this.abilities.length];
        System.arraycopy(abilities.toArray(new Ability[0]), 0, result, 0, abilities.size());
        System.arraycopy(this.abilities, 0, result, abilities.size(), this.abilities.length);

        return result;
    }
}
