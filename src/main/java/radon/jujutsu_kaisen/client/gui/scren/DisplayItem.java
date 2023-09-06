package radon.jujutsu_kaisen.client.gui.scren;

import net.minecraft.world.entity.EntityType;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

import java.util.AbstractMap;

public class DisplayItem {
    public final Type type;
    public Ability ability;
    public AbstractMap.SimpleEntry<EntityType<?>, Integer> curse;
    public CursedTechnique copied;
    public CursedTechnique absorbed;

    public DisplayItem(Type type) {
        this.type = type;
    }

    public DisplayItem(Ability ability) {
        this(Type.ABILITY);

        this.ability = ability;
    }

    public DisplayItem(AbstractMap.SimpleEntry<EntityType<?>, Integer> curse) {
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
