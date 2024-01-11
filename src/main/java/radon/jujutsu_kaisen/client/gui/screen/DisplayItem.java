package radon.jujutsu_kaisen.client.gui.screen;

import net.minecraft.world.entity.EntityType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

import java.util.AbstractMap;
import java.util.List;

public class DisplayItem {
    public final Type type;
    public Ability ability;
    public AbsorbedCurse curse;
    public CursedTechnique copied;
    public CursedTechnique absorbed;

    public DisplayItem(Type type) {
        this.type = type;
    }

    public DisplayItem(Ability ability) {
        this(Type.ABILITY);

        this.ability = ability;
    }

    public DisplayItem(AbsorbedCurse curse) {
        this(Type.CURSE);

        this.curse = curse;
    }

    public DisplayItem(Type type, CursedTechnique technique) {
        this(type);

        if (this.type == Type.COPIED) {
            this.copied = technique;
        } else {
            this.absorbed = technique;
        }
    }

    public enum Type {
        ABILITY,
        CURSE,
        COPIED,
        ABSORBED
    }
}
